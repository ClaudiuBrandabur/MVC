package ro.teamnet.zth.web;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.fmk.AnnotationScanUtils;
import ro.teamnet.zth.fmk.MethodAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Claudiu.Brandabur on 20-Jul-17.
 */
public class MyDispatcherServlet extends HttpServlet {

    private Map<String, MethodAttributes> allowedMethods;

    private <T> void addEntries(Class<T> classes) {
        String path = "";
        Method[] methods;
        String key;
        MethodAttributes attributes;
        MyRequestMethod methodAnnotation;

        //verific daca am adnotarea MyController pe clasa
        if (!classes.isAnnotationPresent(MyController.class))
            return;

        //iau urlPath de pe clasa si concatenez
        path += classes.getAnnotation(MyController.class).urlPath();

        methods = classes.getDeclaredMethods();

        for (Method index : methods)
            if (index.isAnnotationPresent(MyRequestMethod.class)) {

                methodAnnotation = index.getAnnotation(MyRequestMethod.class);

                attributes = new MethodAttributes();
                attributes.setMethodName(index.getName());
                attributes.setControllerClass(classes.getName());
                attributes.setMethodType(methodAnnotation.methodType());

                key = path + methodAnnotation.urlPath() + "/method=" + methodAnnotation.methodType();

                allowedMethods.put(key, attributes);

            }

    }

    public void init() {
        allowedMethods = new HashMap<>();

        try {
            AnnotationScanUtils allClasses = null;
            Iterable<Class> listOfClasses = allClasses.getClasses("ro.teamnet.zth.appl.controller");

            for (Class classes:listOfClasses)
                addEntries(classes);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatchReply(request,response,"GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatchReply(request,response,"POST");
    }

    private void dispatchReply(HttpServletRequest request, HttpServletResponse response, String methodType) throws IOException {
        try {
            Object resultToDisplay = dispatch(request, methodType);
            reply(response, resultToDisplay);
        } catch (Exception e) {
            sendExceptionError(e, response);
        }
    }

    private Object dispatch(HttpServletRequest request, String requestType) {
        String pathInfo = request.getPathInfo();
        MethodAttributes attributes = null;
        String path;

        if (!pathInfo.startsWith("/employees") && !pathInfo.startsWith("/departments") &&
                !pathInfo.startsWith("/jobs") && !pathInfo.startsWith("/locations") )
            throw new RuntimeException("URL-ul nu contine \"/employees\" sau \"/departments\" sau " +
                                    "\"/jobs\" sau \"/locations\"");

        //adaug la URL requestType-ul (GET, POST) ca sa formez cheia din HashMap
        path = pathInfo + "/method=" + requestType;
        //iau valoarea din HashMap pentru cheia path
        attributes = allowedMethods.get(path);

        if (attributes != null) {
            try {
                Class entityClass = Class.forName(attributes.getControllerClass());
                Method returnMethod = entityClass.getMethod(attributes.getMethodName());
                return returnMethod.invoke(entityClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void reply(HttpServletResponse response, Object result) throws IOException {
        response.getWriter().write(String.valueOf(result));

    }

    private void sendExceptionError(Exception e, HttpServletResponse response) throws IOException {
        response.getWriter().write(e.getMessage());
    }

}
