package Java_class;
import Entity.EntityUserInfo;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

public class User_info {
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    private EntityTransaction transaction = entityManager.getTransaction();

    private boolean name_valid(String name) {

        // Check if there is any user with the same name and block him if there is one.

        Query query = entityManager.createQuery("SELECT e FROM EntityUserInfo e WHERE e.nickname = ?1", EntityUserInfo.class);
        query.setParameter(1, name);
        List<EntityUserInfo> list = query.getResultList();
        if (list.isEmpty())
            return true;
        System.out.println("There is an user with this name already, please try new name.");
        return false;

    }
    private boolean mail_valid(String mail){

        //  check if the mail is followed the rule

        boolean tmp1 = false;
        boolean tmp2 = false;
        for (int i=0; i < mail.length();i++){
            if (mail.charAt(i) == '@')
                tmp1 = true;
            if (mail.charAt(i) == '.')
                tmp2 = true;
        }
        if (tmp1  && tmp2)
            return true;
        return false;
    }
    private boolean full_name_valid(String fname, String lname){

        //  checks if the first name and the last name follows the rules

        int first_name_counter = 0;
        int last_name_counter = 0;
        for (int i = 0; i < fname.length(); i++){
            if (fname.charAt(i) >= 'A' && fname.charAt(i) <='z')
                first_name_counter++;
        }
        for (int j = 0; j < lname.length(); j++){
            if (lname.charAt(j) >='A' && lname.charAt(j) <= 'z')
                last_name_counter++;
        }
        if (last_name_counter == lname.length() && first_name_counter == fname.length())
            return true;
        return false;
    }
    private boolean add_user_to_system(String fname, String lname, String nickname, String mail){

        //  add the user info to the user_info DB

        try{
            EntityUserInfo user = new EntityUserInfo();
            user.setFirstName(fname);
            user.setLastName(lname);
            user.setNickname(nickname);
            user.setMail(mail);
            user.setDate(new Date());
            user.setSecretKey(Authenticator.generateSecretKey());
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }
    public boolean add_user_info(String fname, String lname, String nickname, String mail){
        if (full_name_valid(fname, lname)){
            if (mail_valid(mail)){
                if (name_valid(nickname)){
                    add_user_to_system(fname, lname, nickname, mail);
                    return true;
                }
                System.out.println("Your nickname is not valid");
                return false;
            }
            System.out.println("Your mail is not valid");
            return false;
        }
        System.out.println("Your first name / last name is not valid");
        return false;
    }

}
