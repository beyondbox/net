package com.appjumper.silkscreen.net;


import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.bean.AuthInfoResponse;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ConfigResponse;
import com.appjumper.silkscreen.bean.EnterpriseCollectionResponse;
import com.appjumper.silkscreen.bean.EnterpriseDetailsResponse;
import com.appjumper.silkscreen.bean.EnterpriseResponse;
import com.appjumper.silkscreen.bean.EnterpriseSelectResponse;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.bean.EquipmentDetailsResponse;
import com.appjumper.silkscreen.bean.EquipmentListResponse;
import com.appjumper.silkscreen.bean.ExhibitionListResponse;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.bean.IntegralListResponse;
import com.appjumper.silkscreen.bean.KeyWorksResponse;
import com.appjumper.silkscreen.bean.LineDetailsResponse;
import com.appjumper.silkscreen.bean.LineListResponse;
import com.appjumper.silkscreen.bean.MaterProductResponse;
import com.appjumper.silkscreen.bean.MyInquiryDetailsResponse;
import com.appjumper.silkscreen.bean.MyInquiryResponse;
import com.appjumper.silkscreen.bean.MyofferResponse;
import com.appjumper.silkscreen.bean.NewPublicResponse;
import com.appjumper.silkscreen.bean.NewsListResponse;
import com.appjumper.silkscreen.bean.PersonalAuthInfoResponse;
import com.appjumper.silkscreen.bean.PriceDetailsResponse;
import com.appjumper.silkscreen.bean.ProductDetailsResponse;
import com.appjumper.silkscreen.bean.ProductResponse;
import com.appjumper.silkscreen.bean.ProductSpecResponse;
import com.appjumper.silkscreen.bean.ProductTypeResponse;
import com.appjumper.silkscreen.bean.RecruitDetailsResponse;
import com.appjumper.silkscreen.bean.RecruitListResponse;
import com.appjumper.silkscreen.bean.ScoreResponse;
import com.appjumper.silkscreen.bean.ServiceMyListResponse;
import com.appjumper.silkscreen.bean.ServiceProductResponse;
import com.appjumper.silkscreen.bean.TenderDetailsResponse;
import com.appjumper.silkscreen.bean.TenderListResponse;
import com.appjumper.silkscreen.bean.UpdateResponse;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.bean.VerifycodeResponse;

public class JsonParser {
    public static BaseResponse getBaseResponse(String json) {
        BaseResponse rr = JsonUtil.getObject(json, BaseResponse.class);
        return rr;
    }
    public static ConfigResponse getConfigResponse(String json) {
        ConfigResponse rr = JsonUtil.getObject(json, ConfigResponse.class);
        return rr;
    }

    public static ScoreResponse getScoreResponse(String json) {
        ScoreResponse rr = JsonUtil.getObject(json, ScoreResponse.class);
        return rr;
    }

    public static ImageResponse getImageResponse(String json) {
        ImageResponse rr = JsonUtil.getObject(json, ImageResponse.class);
        return rr;
    }

    public static VerifycodeResponse getVerifycodeResponse(String json) {
        VerifycodeResponse rr = JsonUtil.getObject(json, VerifycodeResponse.class);
        return rr;
    }
    public static UpdateResponse getUpdateResponse(String json) {
        UpdateResponse rr = JsonUtil.getObject(json, UpdateResponse.class);
        return rr;
    }

    public static UserResponse getUserResponse(String json) {
        UserResponse rr = JsonUtil.getObject(json, UserResponse.class);
        return rr;
    }

    public static LineListResponse getLineListResponse(String json) {
        LineListResponse rr = JsonUtil.getObject(json, LineListResponse.class);
        return rr;
    }

    public static AreaBeanResponse getAreaBeanResponse(String json) {
        AreaBeanResponse rr = JsonUtil.getObject(json, AreaBeanResponse.class);
        return rr;
    }

    public static EquipmentListResponse getEquipmentListResponse(String json) {
        EquipmentListResponse rr = JsonUtil.getObject(json, EquipmentListResponse.class);
        return rr;
    }

    public static LineDetailsResponse getLineDetailsResponse(String json) {
        LineDetailsResponse rr = JsonUtil.getObject(json, LineDetailsResponse.class);
        return rr;
    }

    public static MyInquiryResponse getMyInquiryResponse(String json) {
        MyInquiryResponse rr = JsonUtil.getObject(json, MyInquiryResponse.class);
        return rr;
    }

    public static MyofferResponse getMyofferResponse(String json) {
        MyofferResponse rr = JsonUtil.getObject(json, MyofferResponse.class);
        return rr;
    }

    public static EnterpriseDetailsResponse getEnterpriseDetailsResponse(String json) {
        EnterpriseDetailsResponse rr = JsonUtil.getObject(json, EnterpriseDetailsResponse.class);
        return rr;
    }

    public static ServiceMyListResponse getServiceMyListResponse(String json) {
        ServiceMyListResponse rr = JsonUtil.getObject(json, ServiceMyListResponse.class);
        return rr;
    }

    public static ProductTypeResponse getProductTypeResponse(String json) {
        ProductTypeResponse rr = JsonUtil.getObject(json, ProductTypeResponse.class);
        return rr;
    }

    public static ServiceProductResponse getServiceProductResponse(String json) {
        ServiceProductResponse rr = JsonUtil.getObject(json, ServiceProductResponse.class);
        return rr;
    }

    public static EquipmentCategoryResponse getEquipmentCategoryResponse(String json) {
        EquipmentCategoryResponse rr = JsonUtil.getObject(json, EquipmentCategoryResponse.class);
        return rr;
    }

    public static EquipmentDetailsResponse getEquipmentDetailsResponse(String json) {
        EquipmentDetailsResponse rr = JsonUtil.getObject(json, EquipmentDetailsResponse.class);
        return rr;
    }

    public static ProductResponse getProductResponse(String json) {
        ProductResponse rr = JsonUtil.getObject(json, ProductResponse.class);
        return rr;
    }

    public static ProductDetailsResponse getProductDetailsResponse(String json) {
        ProductDetailsResponse rr = JsonUtil.getObject(json, ProductDetailsResponse.class);
        return rr;
    }

    public static HomeDataResponse getHomeDataResponse(String json) {
        HomeDataResponse rr = JsonUtil.getObject(json, HomeDataResponse.class);
        return rr;
    }

    public static TenderListResponse getTenderListResponse(String json) {
        TenderListResponse rr = JsonUtil.getObject(json, TenderListResponse.class);
        return rr;
    }

    public static TenderDetailsResponse getTenderDetailsResponse(String json) {
        TenderDetailsResponse rr = JsonUtil.getObject(json, TenderDetailsResponse.class);
        return rr;
    }

    public static ExhibitionListResponse getExhibitionListResponse(String json) {
        ExhibitionListResponse rr = JsonUtil.getObject(json, ExhibitionListResponse.class);
        return rr;
    }

    public static NewsListResponse getNewsListResponse(String json) {
        NewsListResponse rr = JsonUtil.getObject(json, NewsListResponse.class);
        return rr;
    }

    public static RecruitListResponse getRecruitListResponse(String json) {
        RecruitListResponse rr = JsonUtil.getObject(json, RecruitListResponse.class);
        return rr;
    }

    public static RecruitDetailsResponse getRecruitDetailsResponse(String json) {
        RecruitDetailsResponse rr = JsonUtil.getObject(json, RecruitDetailsResponse.class);
        return rr;
    }

    public static AuthInfoResponse getAuthInfoResponse(String json) {
        AuthInfoResponse rr = JsonUtil.getObject(json, AuthInfoResponse.class);
        return rr;
    }

    public static IntegralListResponse getIntegralListResponse(String json) {
        IntegralListResponse rr = JsonUtil.getObject(json, IntegralListResponse.class);
        return rr;
    }

    public static EnterpriseCollectionResponse getEnterpriseCollectionResponse(String json) {
        EnterpriseCollectionResponse rr = JsonUtil.getObject(json, EnterpriseCollectionResponse.class);
        return rr;
    }

    public static KeyWorksResponse getKeyWorksResponse(String json) {
        KeyWorksResponse rr = JsonUtil.getObject(json, KeyWorksResponse.class);
        return rr;
    }

    public static EnterpriseResponse getEnterpriseResponse(String json) {
        EnterpriseResponse rr = JsonUtil.getObject(json, EnterpriseResponse.class);
        return rr;
    }

    public static NewPublicResponse getNewPublicResponse(String json) {
        NewPublicResponse rr = JsonUtil.getObject(json, NewPublicResponse.class);
        return rr;
    }

    public static MyInquiryDetailsResponse getMyInquiryDetailsResponse(String json) {
        MyInquiryDetailsResponse rr = JsonUtil.getObject(json, MyInquiryDetailsResponse.class);
        return rr;
    }

    public static MaterProductResponse getMaterProductResponse(String json) {
        MaterProductResponse rr = JsonUtil.getObject(json, MaterProductResponse.class);
        return rr;
    }

    public static ProductSpecResponse getProductSpecResponse(String json) {
        ProductSpecResponse rr = JsonUtil.getObject(json, ProductSpecResponse.class);
        return rr;
    }
    public static PersonalAuthInfoResponse getPersonalAuthInfoResponse(String json) {
        PersonalAuthInfoResponse rr = JsonUtil.getObject(json, PersonalAuthInfoResponse.class);
        return rr;
    }
    public static PriceDetailsResponse getPriceDetailsResponse(String json) {
        PriceDetailsResponse rr = JsonUtil.getObject(json, PriceDetailsResponse.class);
        return rr;
    }

    public static EnterpriseSelectResponse getEnterpriseSelectResponse(String json) {
        EnterpriseSelectResponse rr = JsonUtil.getObject(json, EnterpriseSelectResponse.class);
        return rr;
    }

}
