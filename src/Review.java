public class Review {
    public int score;
    public String benefits;
    public String disadvantages;
    public String comment;
    public String author;

    public Review(int score, String benefits, String disadvantages, String comment, String author) {
        this.score = score;
        this.benefits = benefits;
        this.disadvantages = disadvantages;
        this.comment = comment;
        this.author = author;
    }
}
