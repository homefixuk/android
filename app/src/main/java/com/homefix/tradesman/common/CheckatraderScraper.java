package com.homefix.tradesman.common;

import android.net.Uri;
import android.util.SparseArray;

import com.samdroid.common.MyLog;
import com.samdroid.common.VariableUtils;
import com.samdroid.listener.interfaces.OnGetListListener;
import com.samdroid.string.Strings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 8/3/2016.
 */

public class CheckatraderScraper implements Runnable {

    public static final String[] locations = new String[]{"st albans", "london", "watford", "enfield", "slough", "chigwell", "staines"};

    public static SparseArray<String> types = new SparseArray<>();

    static {
        types.append(151, "General Plumbing");
        types.append(20, "Plumber");
        types.append(10, "Central Heating Engineer");
        types.append(12, "Electrician");
    }

    private static final String
            TAG = CheckatraderScraper.class.getSimpleName(),
            BASE_SEARCH_URL = "http://www.checkatrade.com/Search/?location=%s&sort=2&page=%s&facet_Sub-categories=%s",
            BASE_PLUMBER_URL = "http://www.checkatrade.com";

    private String location;
    private int jobType, page;

    private OnGetListListener<CheckATrader> callback;
    private MultiGetListener myCallback;

    public CheckatraderScraper(int jobType, String location, int page, OnGetListListener<CheckATrader> callback) {
        this.jobType = jobType;
        this.location = location;
        this.callback = callback;
        this.page = page;
    }

    public static class MultiGetListener<O extends Object> {

        final private List<O> list = new ArrayList<>();
        int numRequests = 0, numGot = 0;
        private OnGetListListener<O> listener;

        public MultiGetListener(int numRequests, OnGetListListener<O> listener) {
            this.numRequests = numRequests;
            this.numGot = 0;
            this.listener = listener;
        }

        public synchronized List<O> getList() {
            return list;
        }

        public void onGotThing(O next) {
            if (next != null) getList().add(next);

            numGot++;

            if (numGot >= numRequests) {
                if (listener != null) listener.onGetListFinished(getList());
            }
        }

    }

    public String getUrl() {
        return String.format(BASE_SEARCH_URL, Uri.encode(location), page, jobType);
    }

    @Override
    public void run() {
        // load the file
        try {
            MyLog.e(TAG, "STARTING SCRAPER for " + jobType + " in " + location);

            // extract all plumbers
            Document doc = Jsoup.connect(getUrl()).get();

            Elements results = doc.getElementsByClass("results__title");

            List<String> hrefs = new ArrayList<>();
            for (int i = 0, len = results.size(); i < len; i++) {
                Element e = results.get(i);
                String html = e.toString();
                final String href = Strings.extractHref(html);

                if (Strings.isEmpty(href)) continue;

                hrefs.add(href);
            }

            myCallback = new MultiGetListener(hrefs.size(), callback);

            // start getting all the tradesman
            for (String href : hrefs)
                new GetTraderThread(href, types.get(jobType), myCallback).start();

        } catch (Exception e) {
            MyLog.printStackTrace(e);
        }

        MyLog.e(TAG, "FINISHED Scraper");
    }

    private class GetTraderThread extends Thread {

        private MultiGetListener<CheckATrader> callback;
        private String href, trade;

        public GetTraderThread(String href, String trade, MultiGetListener<CheckATrader> callback) {
            super();
            this.href = href;
            this.trade = trade;
            this.callback = callback;
        }

        @Override
        public void run() {
            super.run();

            MyLog.e(TAG, "HREF: " + href);

            CheckATrader trader = getPlumber(href, trade);

            MyLog.e(TAG, trader.toString());

            if (callback != null) callback.onGotThing(trader);
        }
    }

    private CheckATrader getPlumber(String refName, String trade) {
        CheckATrader trader = new CheckATrader(trade);

        try {
            if (!refName.startsWith("/")) refName = "/" + refName;
            Document doc = Jsoup.connect(BASE_PLUMBER_URL + refName).get();

            String docText = doc.toString();

            // company name
            Elements companyNameEls = doc.getElementsByTag("h1");
            if (companyNameEls != null) {
                trader.setCompanyName(companyNameEls.text());
            }

            // plumber name
            Elements nameEls = doc.getElementsByClass("contact-card__contact-name");
            if (nameEls != null) {
                trader.setName(nameEls.text());
            }

            // tel 1
            Element phone1Els = doc.getElementById("ctl00_ctl00_content_ctlTel");
            if (phone1Els != null) {
                String phone1 = phone1Els.text();
                phone1 = phone1.replace("tel:", "");
                trader.setPhone(phone1);
            }

            // tel 2
            Element phone2Els = doc.getElementById("ctl00_ctl00_content_ctlTel2");
            if (phone2Els != null) {
                String phone2 = phone2Els.text();
                phone2 = phone2.replace("tel:", "");
                trader.setMobile(phone2);
            }

            // email
            Element emailEls = doc.getElementById("ctl00_ctl00_content_ctlEmail");
            if (emailEls != null) {
                trader.setEmail(emailEls.text());
            }

            // website
            Element websiteEls = doc.getElementById("ctl00_ctl00_content_ctlWeb");
            if (websiteEls != null) {
                trader.setWebsite(websiteEls.text());
            }

            // rating
            Elements ratingEls = doc.getElementsByAttributeValueContaining("class", "feedback-score__average feedback-score__average--large");
            if (ratingEls != null) {
                trader.setRating(VariableUtils.getDoubleSafely(ratingEls.text()));
            }

            // number reviews
            Elements numReviewsEls = doc.getElementsByClass("scores__overall-count");
            if (numReviewsEls != null) {
                String s = numReviewsEls.text();

                s = s.replace("%nbsp;reviews", "");
                s = s.replace(" reviews", "");
                s = s.replace("reviews", "");
                s = s.trim();

                trader.setNumberReviews(s);
            }

            // ticks
            Elements ticksEls = doc.getElementsByClass("member-checksum__item");
            if (ticksEls != null) {
                List<String> ticks = new ArrayList<>();

                for (Element ticksEl : ticksEls) {
                    String s = ticksEl.html();

                    String tickName = Strings.getStringBetween(s, "<span>", "</span>");

                    if (Strings.isEmpty(tickName)) {
                        tickName = s.replace("<i class=\"background-checklist__tick\"></i> <span>", "");
                        tickName = tickName.replace("</span>", "");
                    }

                    if (!Strings.isEmpty(tickName)) ticks.add(tickName);
                }

                trader.setTicks(ticks);
            }

            // base in
            Elements baseDInEls = doc.getElementsByClass("address");
            if (baseDInEls != null) {
                String s = baseDInEls.html();

                String city = Strings.getStringBetween(s, "<span itemprop=\"addressLocality\">", "</span>");
                String region = Strings.getStringBetween(s, "<span itemprop=\"addressRegion\">", "</span>");
                String postcode = Strings.getStringBetween(s, "<span itemprop=\"postalCode\">", "</span>");
                trader.setBasedIn(Strings.combineStrings(", ", city, region, postcode));
            }

            // works in
            Elements worksInEls = doc.getElementsByClass("member-profile__works-in");
            if (worksInEls != null) {
                String s = worksInEls.html();

                String worksIn = Strings.getStringBetween(s, "<p>", "</p>");
                worksIn = worksIn.replace("\"", "");
                trader.setWorksIn(worksIn);
            }

        } catch (Exception e) {
            MyLog.e(TAG, "Href: " + refName);
        }

        return trader;
    }

    public static class CheckATrader {

        public String trade, companyName, name, basedIn, email, website, phone, mobile, worksIn, numberReviews;
        public double rating;
        List<String> ticks;

        public CheckATrader(String trade) {
            this.trade = trade;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBasedIn() {
            return basedIn;
        }

        public void setBasedIn(String basedIn) {
            this.basedIn = basedIn;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public String getNumberReviews() {
            return numberReviews;
        }

        public void setNumberReviews(String numberReviews) {
            this.numberReviews = numberReviews;

            this.numberReviews = this.numberReviews.replace(" ", "");
            this.numberReviews = this.numberReviews.replace(",", "");
            this.numberReviews = this.numberReviews.replace(".", "");
        }

        public List<String> getTicks() {
            return ticks;
        }

        public void setTicks(List<String> ticks) {
            this.ticks = ticks;
        }

        public String getWorksIn() {
            return worksIn;
        }

        public void setWorksIn(String worksIn) {
            this.worksIn = worksIn;
        }

        @Override
        public String toString() {
            return companyName + ", " + name + ", " + phone + ", " + mobile + ", " + email + ", " + website + ", " + rating + ", " + numberReviews + ", " + basedIn + ", " + worksIn + " ticks: " + VariableUtils.listToString(ticks);
        }

        public String toCsvString() {
            return "\"" + trade + "\",\"" + companyName + "\",\"" + name + "\",\"" + phone + "\",\"" + mobile + "\",\"" + email + "\",\"" + website + "\",\"" + rating + "\",\"" + numberReviews + "\",\"" + basedIn + "\",\"" + worksIn + "\",\"" + VariableUtils.listToString(ticks) + "\"";
        }

    }

}
