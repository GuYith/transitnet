package whu.edu.cs.transitnet.vo;

import whu.edu.cs.transitnet.Torch.base.visualization.TrajJsonModel;

public class RoutesVo {
    private String routeId;
    private String agencyId;
    private String routeShortName;
    private String routeLongName;
    private String routeDesc;
    private String routeType;
    private String routeColor;
    private String routeTextColor;
    private TrajJsonModel trajJsonModel;

    public RoutesVo(String routeId, String agencyId, String routeShortName, String routeLongName, String routeDesc, String routeType, String routeColor, String routeTextColor) {
        this.routeId = routeId;
        this.agencyId = agencyId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeDesc = routeDesc;
        this.routeType = routeType;
        this.routeColor = routeColor;
        this.routeTextColor = routeTextColor;
        this.trajJsonModel = null;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    public String getRouteTextColor() {
        return routeTextColor;
    }

    public void setRouteTextColor(String routeTextColor) {
        this.routeTextColor = routeTextColor;
    }

    public TrajJsonModel getTrajJsonModel() {
        return trajJsonModel;
    }

    public void setTrajJsonModel(TrajJsonModel trajJsonModel) {
        this.trajJsonModel = trajJsonModel;
    }
}
