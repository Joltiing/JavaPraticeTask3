public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread th = new Thread(()->{
            try {
                var reviews = Parser.parse("https://nanegative.ru/ozon-ru-otzivy",1);
                for (var review : reviews){
                    System.out.println(review.author);
                    System.out.println(review.score);
                    System.out.println(review.benefits);
                    System.out.println(review.disadvantages);
                    System.out.println(review.comment);
                    System.out.println("________________________________");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        th.start();
        th.join();
    }
}