package ro.teamnet.zth.appl.controller;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;

/**
 * Created by Claudiu.Brandabur on 20-Jul-17.
 */
@MyController(urlPath = "/locations")
public class LocationController {

    @MyRequestMethod(urlPath = "/all", methodType = "GET")
    public String getAllLocations () {
        return "allLocations";
    }

    @MyRequestMethod(urlPath = "/one", methodType = "GET")
    public String getOneLocation () {
        return "oneRandomLocation";
    }

}
