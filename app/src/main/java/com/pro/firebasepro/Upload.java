package com.pro.firebasepro;

public class Upload {
        private String imageName;
        private String imageUrl;
        private String imageId;
        private String eventId;

        public Upload() {
            //empty constructor needed
        }

    public Upload(String imageName, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public Upload(String imageName, String imageUrl, String imageId, String eventId) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
        this.eventId = eventId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
