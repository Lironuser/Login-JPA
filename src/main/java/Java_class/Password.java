package Java_class;

import Entity.EntityUserInfo;
import Entity.EntityUserPassword;
import jakarta.persistence.*;

import java.util.List;

public class Password {
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    private EntityTransaction transaction = entityManager.getTransaction();


    public int get_user_id(String name){

        //  return the id from the user_info entity

        try{
            Query query = entityManager.createQuery("SELECT e FROM EntityUserInfo  e WHERE e.nickname = ?1", EntityUserInfo.class);
            query.setParameter(1, name);
            List<EntityUserInfo> list = query.getResultList();
            if (list.isEmpty()){
                System.out.println("Your user not found");
                return -1;
            }
            return list.get(0).getId();
        }catch (Exception e){
            System.out.println(e);
        }
        return -1;
    }
    private String get_user_key(String name){

        //  return the secret key from the user_info entity

        try{
            Query query = entityManager.createQuery("SELECT e FROM EntityUserInfo e WHERE e.nickname = ?1", EntityUserInfo.class);
            query.setParameter(1, name);
            List<EntityUserInfo> list = query.getResultList();
            if (list.isEmpty()){
                System.out.println("Your user 'key' was not found");
            }
            return list.get(0).getSecretKey();
        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println("Error in 'get_user_key'");
        return null;
    }


    private boolean password_valid(String pass){

        //  check if the password is follow the rules

        boolean flag = false;
        if (pass.length() >= 8 && pass.length() <= 16){
            for (int i = 0; i < pass.length(); i++){
                if (pass.charAt(i) >= 'A' && pass.charAt(i) <= 'Z') {
                    flag = true;
                }
                if (pass.charAt(i) >= '0' && pass.charAt(i) <= '9' && flag == true) return true;
            }
        }
        else{
            System.out.println("Your password incorrect!");
        }
        return false;
    }
    private boolean add_password_to_system(String nickname, String password){

        //  add the password to the DB

        try{
            EntityUserPassword pass = new EntityUserPassword();
            int id = get_user_id(nickname);
            if (id == -1) return false;
            pass.setUserId(id);
            pass.setPassword(password);
            transaction.begin();
            entityManager.persist(pass);
            transaction.commit();
            return true;
        }catch (Exception e){
            System.out.println(e);
        }
        return false;
    }



    public boolean add_password(String name, String pass){
        if (password_valid(pass)){
                add_password_to_system(name, pass);
                return true;
            }
        return true;
    }

}
