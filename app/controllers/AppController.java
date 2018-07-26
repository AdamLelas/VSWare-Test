package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.db.Database;
import play.mvc.*;
import views.html.*;
import views.html.auth.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AppController extends Controller {


    @Inject
    private Database db;

    @Inject
    FormFactory formFactory;


    public Result index() {
        return ok("First Page");
    }

    public Result users(){
        List<User> users = ConnectToDB();
        if(users ==null){
            return notFound("User Not Found");
        }
        return ok(showusers.render(users));
    }

    public Result save() {
        return ok("saved");
    }

    public Result auth(){
        List<User> users = ConnectToDB();

        Form<User>  userForm = formFactory.form(User.class).bindFromRequest();
        User fUser = userForm.get();

        if(users ==null){
            return notFound("User Not Found");
        }
        System.out.println(" before For Loop");
        System.out.println("fUsername " + fUser.getUsername());


        for(User user : users){
            System.out.println("For Loop");
            if(user.getUsername().equals(fUser.username)){
                System.out.println("if user");
                if(user.getPassword().equals(fUser.password)){
                    System.out.println("if password");
                    return redirect(routes.HomeController.index());
                }
                else{
                    return redirect(routes.AppController.login());
                }
            }
        }
        return redirect(routes.AppController.login());

    }

    public Result login(){
        Form<User> userForm = formFactory.form(User.class);
        return ok(login.render(userForm));
    }



    public List<User> ConnectToDB(){
        System.out.println("Before Try block");

        List<User> users = new ArrayList<>();
        Connection conn = db.getConnection();

        System.out.println("Before Try block");
        try{
            System.out.println("Inside Try block");

            ResultSet rs = conn.prepareStatement("select * from user").executeQuery();
            System.out.println("After Execute Query");

            while(rs.next()){
                System.out.println(rs.getString("username"));

                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try{
                conn.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
        return users;
    }
}
