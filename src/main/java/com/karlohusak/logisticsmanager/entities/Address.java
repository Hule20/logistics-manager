package com.karlohusak.logisticsmanager.entities;

public class Address {
    private String street;
    private String houseNumber;
    private String city;
    private String latitude;
    private String longitude;
    private String country;
    private String displayName;

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public String getDisplayName() {
        return displayName;
    }

    private Address(AddressBuilder builder) {
        this.street = builder.street;
        this.houseNumber = builder.houseNumber;
        this.city = builder.city;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.country = builder.country;
        this.displayName = builder.displayName;
    }

    @Override
    public String toString() {
        if (houseNumber == null) {
            return street + ", " + city + ", " + country;
        } else {
            return street + " " + houseNumber + ", " + city + ", " + country;
        }
    }

    public static class AddressBuilder {
        private String street;
        private String houseNumber;
        private String city;
        private String latitude;
        private String longitude;
        private String country;
        private String displayName;

        public AddressBuilder setStreet(String street) {
            this.street = street;
            return this;
        }

        public AddressBuilder setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public AddressBuilder setCity(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public AddressBuilder setLongitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public AddressBuilder setCountry(String country) {
            this.country = country;
            return this;
        }

        public AddressBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
