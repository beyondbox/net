package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/15.
 * 版本更新
 */
public class Update extends BaseResponse{
        private String versioncode;
        private String id;
        private String content;
        private String url;

        public String getVersioncode() {
            return versioncode;
        }

        public void setVersioncode(String versioncode) {
            this.versioncode = versioncode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
}
