import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps with parsing "nanegative.ru".
 */
public class Parser {
    /**
     * Parses page and scans for reviews.
     * @param url Path to web-site.
     * @return Collection of reviews.
     * @throws Exception Just any exception :D
     */
    public static ArrayList<Review> parse(String url, int maxPageCount) throws Exception {
        if(url.isEmpty()){
            throw new Exception("Empty url!");
        }

        try {
            //System.setProperty("webdriver.edge.driver", "selenium/msedgedriver.exe");
            WebDriverManager.edgedriver().setup();
            var driver = new EdgeDriver();
            driver.get(url);

            var page = driver.findElement(By.className("pagination-holder"));
            var pagesCount = page != null
                    ? getNumberPages(page)
                    : 1;

            if (pagesCount>maxPageCount)
                pagesCount=maxPageCount;
            var reviews = new ArrayList<Review>();

            for (int i = 1; i <= pagesCount; i++) {
                if (i != 1) {
                    driver.get(url + "?page=" + i);
                    Thread.sleep(500);
                }

                var reviewList = driver.findElements(By.className("reviewers-box"));

                var reviewsOnThePage = getReviews(reviewList);
                reviews.addAll(reviewsOnThePage);
            }

            driver.quit();

            return reviews;
        }
        catch (Exception ex){
            throw new Exception("Something went wrong...",ex);
        }
    }

    /**
     * Gets review rating from tags.
     * @param spanTags List of tags.
     * @return Integer value of review rating.
     */
    private static int getRating(List<WebElement> spanTags) {
        var score = 0;
        var isFound = false;

        for(var i=0;(i < spanTags.size()) && (!isFound);i++){
            var element = spanTags.get(i);

            if (element.getAttribute("itemprop").equals("ratingValue")) {
                score = Integer.parseInt(element.getText());
                isFound = true;
            }
        }

        return score;
    }

    /**
     * Gets reviews and theirs descriptions.
     * @param reviewList List of web-elements containing reviews.
     * @return Collection of reviews.
     */
    private static ArrayList<Review> getReviews(List<WebElement> reviewList) {
        var reviews = new ArrayList<Review>();

        for (var review : reviewList) {
            var reviewer = review.findElement(By.className("name"));
            var spanTags = reviewer.findElements(By.tagName("span"));
            var score = getRating(spanTags);
            var author = getAuthor(spanTags);

            var textBlock = review.findElements(By.tagName("tr"));
            var benefits = "";
            var disadvantages = "";
            var comment = "";

            for (int i = 0; i < textBlock.size(); i++) {
                var block = textBlock.get(i);
                var nodes = block.findElements(By.tagName("td"));

                if (i == 0) {
                    benefits = getTextFromNode(nodes);
                }
                else if (i == 1) {
                    disadvantages = getTextFromNode(nodes);
                }
                else {
                    comment = getTextFromNode(nodes);
                }
            }

            reviews.add(new Review(score, benefits, disadvantages, comment,author));
        }

        return reviews;
    }

    private static String getAuthor(List<WebElement> spanTags) {
        var author = "";
        var isFound = false;

        for(var i=0;(i < spanTags.size()) && (!isFound);i++){
            var element = spanTags.get(i);

            if (element.getAttribute("itemprop").equals("author")) {
                author = element.getText();
                isFound = true;
            }
        }

        return author;
    }

    /**
     * Gets text containing in node.
     * @param nodes List of nodes to process.
     * @return String value of node content.
     */
    private static String getTextFromNode(List<WebElement> nodes) {
        var text = new StringBuilder();

        for (int i = 1; i < nodes.size(); i++) {
            text.append(nodes.get(i).getText());
        }

        return text.toString();
    }

    /**
     * Gets number of pages with reviews.
     * @param page Root page web-element.
     * @return Integer value of pages count.
     */
    private static int getNumberPages(WebElement page) {
        var allPagesClass = page.findElement(By.className("all-pages"));
        var messageAboutNumberOfPages = allPagesClass.getText();

        int indexLastSpace = messageAboutNumberOfPages.lastIndexOf(" ");

        return indexLastSpace > -1
                ?  Integer.parseInt(messageAboutNumberOfPages.substring(indexLastSpace + 1))
                : 1;
    }

}
