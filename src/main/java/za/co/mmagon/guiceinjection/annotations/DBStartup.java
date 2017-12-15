package za.co.mmagon.guiceinjection.annotations;

import java.lang.annotation.*;

@Target(
		{
				ElementType.TYPE, ElementType.TYPE_USE
		})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DBStartup
{
}
