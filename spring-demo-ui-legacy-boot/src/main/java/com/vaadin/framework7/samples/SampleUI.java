package com.vaadin.framework7.samples;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.framework7.samples.about.AboutView;
import com.vaadin.framework7.samples.authentication.AccessControl;
import com.vaadin.framework7.samples.authentication.LoginScreen;
import com.vaadin.framework7.samples.crud.SampleCrudView;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@SuppressWarnings("unused")
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@SpringUI
public class SampleUI extends UI {

    @Autowired
    private AccessControl accessControl;

    @Autowired
    private SpringViewProvider viewProvider;

    private Menu menu;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("My");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, this::showMainView));
        } else {
            showMainView();
        }
    }

    private void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);

        HorizontalLayout layout = new HorizontalLayout();
        setContent(layout);

        layout.setStyleName("main-screen");

        CssLayout springViewDisplay = new CssLayout();
        springViewDisplay.addStyleName("valo-content");
        springViewDisplay.setSizeFull();

        Navigator navigator = new Navigator(this, springViewDisplay);
        navigator.addProvider(viewProvider);
        navigator.setErrorView(ErrorView.class);

        menu = new Menu(navigator);
        // View are registered automatically by Vaadin Spring support
        menu.addViewButton(SampleCrudView.VIEW_NAME, SampleCrudView.VIEW_NAME,
                FontAwesome.EDIT);
        menu.addViewButton(AboutView.VIEW_NAME, AboutView.VIEW_NAME,
                FontAwesome.INFO_CIRCLE);

        navigator.addViewChangeListener(new ViewChangeHandler());

        layout.addComponent(menu);
        layout.addComponent(springViewDisplay);
        layout.setExpandRatio(springViewDisplay, 1);
        layout.setSizeFull();

        navigator.navigateTo(SampleCrudView.VIEW_NAME);
    }

    private class ViewChangeHandler implements ViewChangeListener {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    }

}
