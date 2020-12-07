package com.lt.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TushareResult {

    private String code;
    private String msg;
    private Data data;

    public class Data{
        private String has_more;
        private List<String> fields;
        private List<List<String>> items;

        public String getHas_more() {
            return has_more;
        }

        public void setHas_more(String has_more) {
            this.has_more = has_more;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public List<List<String>> getItems() {
            return items;
        }

        public void setItems(List<List<String>> items) {
            this.items = items;
        }
    }
}
