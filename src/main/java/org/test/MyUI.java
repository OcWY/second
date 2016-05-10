package org.test;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("org.test.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        Button button = new Button("list messages");
        button.addClickListener( e -> {
            verticalLayout.removeAllComponents();
            Client c = ClientBuilder.newClient();
            try {
                String responseMsg = c.target("http://localhost:9000/api/json/get").request().get(String.class);
                if(responseMsg.isEmpty()){
                    verticalLayout.addComponent(new Label("no any message yet"));
                } else {
                    verticalLayout.addComponent(new Label(responseMsg));
                }
            } catch (Exception e1) {
                verticalLayout.addComponent(new Label("Can not get response"+e1.toString()));
            }
        });
        final TextField name = new TextField();
        name.setCaption("Type your name here:");
        final TextField title = new TextField();
        title.setCaption("Type your title here:");
        final TextField sender = new TextField();
        sender.setCaption("Type your sender here:");
        final TextField url = new TextField();
        url.setCaption("Type your url here:");

        Button cButton = new Button("create message");
        cButton.addClickListener(e ->{
            verticalLayout.removeAllComponents();

            if(!name.isEmpty() && !title.isEmpty() && !sender.isEmpty() && !url.isEmpty()){
                Client c = ClientBuilder.newClient();
                String input = "{\"name\":\""+name.getValue()+"\",\"title\":\""+title.getValue()+"\",\"sender\":\""+sender.getValue()+"\",\"url\":\""+url.getValue()+"\"}";
                Response responseMsg = c.target("http://localhost:9000/api/json/post").request().post(Entity.entity(input, MediaType.APPLICATION_JSON)  );
                if (responseMsg.getStatus() != 201) {
                    verticalLayout.addComponent(new Label(new RuntimeException("Failed : HTTP error code : "
                            + responseMsg.getStatus()).toString()));
                } else {
                    verticalLayout.addComponent(new Label("message saved"));
                }
                name.clear();
                title.clear();
                sender.clear();
                url.clear();
            } else {
                verticalLayout.addComponent(new Label("all the field need to be not empty in order to create a message"));
            }
        });
        layout.addComponents(button,name,title,sender,url,cButton,verticalLayout);
        layout.setMargin(true);
        layout.setSpacing(true);

        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
