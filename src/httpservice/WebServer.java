package httpservice;

import cc.frontend.common.TGRConfig;
import cc.frontend.controller.IndexController;
import cc.frontend.controller.InfoController;
import cc.frontend.controller.PaymentController;
import cc.frontend.controller.SaleCardController;
import cc.frontend.controller.TopupGameController;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 *
 * @author tunm
 */
public class WebServer extends Thread {

    private static Logger logger_ = Logger.getLogger(WebServer.class);

    @Override
    public void run() {
        try {
            this.startWebServer();
        } catch (Exception ex) {
            logger_.error("Webserver error", ex);
        }
    }

    public void startWebServer() throws Exception {
        int port = Integer.valueOf(System.getProperty("zport"));
        if (port == 0) {
            System.exit(-1);
        }
        int acceptors = Integer.parseInt(TGRConfig.gJettyThreadPool.getAcceptors());
        int min_threads = Integer.parseInt(TGRConfig.gJettyThreadPool.getMinPool());
        int max_threads = Integer.parseInt(TGRConfig.gJettyThreadPool.getMaxPool());

        if (port == 0) {
            logger_.error("zport not found");
            System.exit(-1);
        }
        logger_.info("get rest listen_port from zport=" + port);

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(max_threads);
        threadPool.setMinThreads(min_threads);
        Server server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setIdleTimeout(60000);
        server.setConnectors(new Connector[]{connector});
        
        ServletHandler handler = new ServletHandler();
//        connector.setStatsOn(false);
//        connector.setLowResourcesConnections(20000);
//        connector.setLowResourcesMaxIdleTime(5000);
//        connector.setAcceptors(acceptors);
        //viewer servlet
        ServletHolder holderSelling = handler.addServletWithMapping(SaleCardController.class, "/banthe/*");
        holderSelling.setAsyncSupported(true);
        ServletHolder holderPay = handler.addServletWithMapping(PaymentController.class, "/thanhtoan/*");
        holderPay.setAsyncSupported(true);
        ServletHolder holderTopup = handler.addServletWithMapping(TopupGameController.class, "/naptiengame/*");
        holderTopup.setAsyncSupported(true);
        ServletHolder holderInfo = handler.addServletWithMapping(InfoController.class, "/thongtin/*");
        holderInfo.setAsyncSupported(true);
        ServletHolder holderHome = handler.addServletWithMapping(IndexController.class, "/");
        holderHome.setAsyncSupported(true);

        //ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(handler);
        server.setStopTimeout(1000);

        ShutdownThread obj = new ShutdownThread(server);
        Runtime.getRuntime().addShutdownHook(obj);
        
        server.start();
        server.join();
    }
}

class ShutdownThread extends Thread {

    private Server server;
    private static Logger logger_ = Logger.getLogger(ShutdownThread.class);

    public ShutdownThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        logger_.info("Waiting for shut down!");
        try {
            server.stop();
        } catch (Exception ex) {
            logger_.error(ex.getMessage());
        }
        logger_.info("Server shutted down!");
    }
}
