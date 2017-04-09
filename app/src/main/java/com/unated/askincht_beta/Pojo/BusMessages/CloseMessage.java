package com.unated.askincht_beta.Pojo.BusMessages;


public class CloseMessage {

        private String rating;
        private String shop_id;
        public CloseMessage(String shop_id, String rating) {
            this.shop_id=shop_id;
            this.rating=rating;

        }
        public String getRating(){
            return rating;
        }
        public String getShop_id(){
            return shop_id;
        }

    }

