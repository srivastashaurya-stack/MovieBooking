package bookingengine;

public class Show {
    private Long id;
    private Long movieId;
    private int screenNumber;
    private String showTime;
    private double ticketPrice;

    public Show() {
    }

    public Show(Long id, Long movieId, int screenNumber, String showTime, double ticketPrice) {
        this.id = id;
        this.movieId = movieId;
        this.screenNumber = screenNumber;
        this.showTime = showTime;
        this.ticketPrice = ticketPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public void setScreenNumber(int screenNumber) {
        this.screenNumber = screenNumber;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}