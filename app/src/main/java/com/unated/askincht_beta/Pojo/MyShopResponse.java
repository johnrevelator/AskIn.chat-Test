package com.unated.askincht_beta.Pojo;


public class MyShopResponse {
    private int status;
    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }


    public class Data {
        private MyShop shop;

        public MyShop getShop() {
            return shop;
        }

        public class MyShop {
            private int id;
            private int phone;
            private String description;
            private String name;
            private String avatar;

            public int getId() {
                return id;
            }
            public int getPhone() {
                return phone;
            }
            public String getName() {

                return name;}
            public String getAvatar() {

                return avatar;}
            public String getDesc() {

                return description;}
            //Остальные поля пока не нужны
        }
    }
}
