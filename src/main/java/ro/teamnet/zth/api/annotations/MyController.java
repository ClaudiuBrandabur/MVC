package ro.teamnet.zth.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Claudiu.Brandabur on 20-Jul-17.
 */

@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Documented
public @interface MyController {

    String urlPath();

}
