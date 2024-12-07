package Models;


public class BannerItem {
    private final String title;
    private final String discountText;
    private final String buttonText;
    private final int backgroundColor;
    private final int iamgeview;

    public BannerItem(String title, String discountText, String buttonText, int backgroundColor, int iamgeview) {
        this.title = title;
        this.discountText = discountText;
        this.buttonText = buttonText;
        this.backgroundColor = backgroundColor;
        this.iamgeview = iamgeview;
    }

    public String getTitle() {
        return title;
    }

    public String getDiscountText() {
        return discountText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public int getIamgeview() {
        return iamgeview;
    }

    public int getBackgroundColor() {
        return backgroundColor;
}
}