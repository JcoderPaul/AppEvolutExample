package me.oldboy.market.config.tomcat;

import me.oldboy.market.config.utils.ConfigProvider;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Класс для запуска и управления встроенным (embedded) сервером Tomcat.
 * Обеспечивает программную конфигурацию и запуск веб-приложения без необходимости
 * внешнего установленного Tomcat сервера.
 *
 * @see Tomcat
 * @see ConfigProvider
 */
public class EmbeddedTomcat {
    /**
     * Экземпляр встроенного сервера Tomcat
     */
    private Tomcat tomcat;
    /**
     * Поставщик конфигурационных параметров сервера
     */
    private ConfigProvider configProvider;

    /**
     * Создает новый экземпляр EmbeddedTomcat с указанными параметрами.
     *
     * @param tomcat         экземпляр Tomcat для управления
     * @param configProvider поставщик конфигурационных параметров
     */
    public EmbeddedTomcat(Tomcat tomcat, ConfigProvider configProvider) {
        this.tomcat = tomcat;
        this.configProvider = configProvider;
    }

    /**
     * Запускает встроенный сервер Tomcat с предварительной конфигурацией.
     * Выполняет настройку всех параметров сервера, контекста и сканирования аннотаций.
     *
     * @throws Exception если произошла ошибка при запуске сервера
     */
    public void startServer() throws Exception {
        /* Конфигурируем экземпляр Tomcat */
        tomcat.setBaseDir("TomCat");
        tomcat.setHostname(configProvider.get("http.server_ip"));
        tomcat.setPort(Integer.parseInt(configProvider.get("http.server_port")));
        tomcat.getConnector();

        /* Указываем временную директорию (можно оставить null) */
        String webAppDirLocation = "src/main/web/";
        /* Контекст ("/" = root) */
        Context context = tomcat.addWebapp("", new File(webAppDirLocation).getAbsolutePath());

        /* --- КЛЮЧЕВАЯ ЧАСТЬ: Включение сканирования аннотаций --- */
        context.setParentClassLoader(EmbeddedTomcat.class.getClassLoader());

        /* Включаем сканирование JAR файлов */
        context.setJarScanner(new org.apache.tomcat.util.scan.StandardJarScanner());

        /* Отключаем проверку XML дескрипторов */
        context.setXmlValidation(false);
        context.setXmlNamespaceAware(false);

        /* Включаем сканирование аннотаций */
        context.addLifecycleListener(new Tomcat.FixContextListener());

        /* Ключевые параметры для сканирования аннотаций */
        context.setAddWebinfClassesResources(true);

        /* Отключаем metadata-complete для чтения аннотаций */
        context.addParameter("metadata-complete", "false");

        /* Включаем сканирование классов для аннотаций */
        org.apache.tomcat.util.scan.StandardJarScanner jarScanner =
                (org.apache.tomcat.util.scan.StandardJarScanner) context.getJarScanner();
        jarScanner.setScanAllDirectories(true);
        jarScanner.setScanAllFiles(true);
        jarScanner.setScanClassPath(true);

        /* Указываем, где лежат .class файлы */
        File additionWebInfClasses = new File("build/classes");
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
        context.setResources(resources);

        /* Запуск Tomcat */
        tomcat.start();
        System.out.println("Tomcat запущен.");

        /* Держим приложение живым */
        tomcat.getServer().await();
    }

    /**
     * Останавливает встроенный сервер Tomcat.
     * Выполняет мягкую остановку сервера с освобождением всех ресурсов.
     *
     * @throws RuntimeException если произошла ошибка при остановке сервера
     */
    public void stopServer() {
        try {
            tomcat.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Tomcat остановлен.");
    }
}