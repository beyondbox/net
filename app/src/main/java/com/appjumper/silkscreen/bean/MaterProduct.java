package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/7.
 */
public class MaterProduct {
    private String id;
    private String name;

    /**
     * allowDelete : true
     */

    private String allowDelete;
    /**
     * isIndex : true
     */

    private boolean isIndex=true;

    public String isAllowDelete() {
        if(allowDelete!=null){
            return allowDelete;
        }else{
            if(id.equals("1")){
                return "0";
            }else{
                return "1";
            }
        }

    }

    public void setAllowDelete(String allowDelete) {
        if(allowDelete!=null&&!allowDelete.equals("")){
            this.allowDelete = allowDelete;
        }else{
            if(id.equals("1")){
                this.allowDelete = "0";
            }else{
                this.allowDelete = "1";
            }

        }
    }

    public boolean getIsIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj==null)
            return false;
        if(this == obj){
            return true;
        }
        if (obj instanceof MaterProduct) {
            MaterProduct other = (MaterProduct) obj;
            return  (other.name).equals(this.name)&&(other.id).equals(this.id);
        }
        return false;
    }

}
