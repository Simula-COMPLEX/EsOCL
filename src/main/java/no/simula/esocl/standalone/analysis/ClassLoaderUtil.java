package no.simula.esocl.standalone.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class ClassLoaderUtil {
    /**
     * Thread.currentThread().getContextClassLoader().getResource("")
     */

    /**
     * ����Java�ࡣ ʹ��ȫ�޶�����
     *
     * @return
     * @paramclassName
     */
    public static Class loadClass(String className) {
        try {
            return getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found '" + className + "'", e);
        }
    }

    /**
     * �õ��������
     *
     * @return
     */
    public static ClassLoader getClassLoader() {

        return ClassLoaderUtil.class.getClassLoader();
    }

    /**
     * �ṩ�����classpath����Դ·���������ļ���������
     *
     * @return �ļ�������
     * @paramrelativePath���봫����Դ�����·�����������classpath��·���������Ҫ����classpath�ⲿ����Դ����Ҫʹ��../������
     * @throwsIOException
     * @throwsMalformedURLException
     */
    public static InputStream getStream(String relativePath)
            throws MalformedURLException, IOException {
        if (!relativePath.contains("../")) {
            return getClassLoader().getResourceAsStream(relativePath);

        } else {
            return ClassLoaderUtil.getStreamByExtendResource(relativePath);
        }

    }

    /**
     * @return
     * @paramurl
     * @throwsIOException
     */
    public static InputStream getStream(URL url) throws IOException {
        if (url != null) {

            return url.openStream();

        } else {
            return null;
        }
    }

    /**
     * @return
     * @paramrelativePath���봫����Դ�����·�����������classpath��·���������Ҫ����classpath�ⲿ����Դ����Ҫʹ��../������
     * @throwsMalformedURLException
     * @throwsIOException
     */
    public static InputStream getStreamByExtendResource(String relativePath)
            throws MalformedURLException, IOException {
        return ClassLoaderUtil.getStream(ClassLoaderUtil
                .getExtendResource(relativePath));

    }

    /**
     * �ṩ�����classpath����Դ·�����������Զ�������һ��ɢ�б�
     *
     * @return
     * @paramresource
     */
    public static Properties getProperties(String resource) {
        Properties properties = new Properties();
        try {
            properties.load(getStream(resource));
        } catch (IOException e) {
            throw new RuntimeException("couldn't load properties file '"
                    + resource + "'", e);
        }
        return properties;
    }

    /**
     * �õ���Class���ڵ�ClassLoader��Classpat�ľ���·���� URL��ʽ��
     *
     * @return
     */
    public static String getAbsolutePathOfClassLoaderClassPath() {

        return ClassLoaderUtil.getClassLoader().getResource("").toString();

    }

    /**
     * @paramrelativePath ���봫����Դ�����·�����������classpath��·���������Ҫ����classpath�ⲿ����Դ����Ҫʹ��../������
     * @return��Դ�ľ���URL
     * @throwsMalformedURLException
     */
    public static URL getExtendResource(String relativePath)
            throws MalformedURLException {

        // ClassLoaderUtil.log.info(Integer.valueOf(relativePath.indexOf("../"))) ;
        if (!relativePath.contains("../")) {
            return ClassLoaderUtil.getResource(relativePath);

        }
        String classPathAbsolutePath = ClassLoaderUtil
                .getAbsolutePathOfClassLoaderClassPath();
        if (relativePath.substring(0, 1).equals("/")) {
            relativePath = relativePath.substring(1);
        }

        String wildcardString = relativePath.substring(0,
                relativePath.lastIndexOf("../") + 3);
        relativePath = relativePath
                .substring(relativePath.lastIndexOf("../") + 3);
        int containSum = ClassLoaderUtil.containSum(wildcardString, "../");
        classPathAbsolutePath = ClassLoaderUtil.cutLastString(
                classPathAbsolutePath, "/", containSum);
        String resourceAbsolutePath = classPathAbsolutePath + relativePath;

        URL resourceAbsoluteURL = new URL(resourceAbsolutePath);
        return resourceAbsoluteURL;
    }

    /**
     * @return
     * @paramsource
     * @paramdest
     */
    private static int containSum(String source, String dest) {
        int containSum = 0;
        int destLength = dest.length();
        while (source.contains(dest)) {
            containSum = containSum + 1;
            source = source.substring(destLength);

        }

        return containSum;
    }

    /**
     * @return
     * @paramsource
     * @paramdest
     * @paramnum
     */
    private static String cutLastString(String source, String dest, int num) {
        // String cutSource=null;
        for (int i = 0; i < num; i++) {
            source = source.substring(0,
                    source.lastIndexOf(dest, source.length() - 2) + 1);
        }

        return source;
    }

    /**
     * @return
     * @paramresource
     */
    public static URL getResource(String resource) {

        return ClassLoaderUtil.getClassLoader().getResource(resource);
    }

    /**
     * @paramargs
     * @throwsMalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException {

        // ClassLoaderUtil.getExtendResource("../spring/dao.xml");
        // ClassLoaderUtil.getExtendResource("../../../src/log4j.properties");
        ClassLoaderUtil.getExtendResource("log4j.properties");

        System.out.println(ClassLoaderUtil.getClassLoader()
                .getResource("log4j.properties").toString());

    }

}
