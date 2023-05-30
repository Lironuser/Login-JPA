package Java_class;

import Entity.EntityUserInfo;
import Entity.EntityUserPassword;
import Entity.EntityLogs;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Log {
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();
    private EntityTransaction transaction = entityManager.getTransaction();

    private boolean get_correct_time(long userID, String password) {

        //Check if the user wrong with the password over 3 times in 15 min.

        Date now = new Date();
        TypedQuery<EntityLogs> queryThreeLast = entityManager.createQuery("Select t from EntityLogs t where t.id = ?1", EntityLogs.class);
        queryThreeLast.setParameter(1, userID);
        List<EntityLogs> listTimes = queryThreeLast.getResultList();
        if (listTimes.size() < 3 || (listTimes.get(listTimes.size() - 1).isSuccess() || (listTimes.get(listTimes.size() - 2).isSuccess() || (listTimes.get(listTimes.size() - 3).isSuccess()))))
            return true;
        if (!listTimes.get(listTimes.size() - 1).isSuccess() || !listTimes.get(listTimes.size() - 2).isSuccess() || !listTimes.get(listTimes.size() - 3).isSuccess()) {
            long diffInMilliseconds = Math.abs(now.getTime() - listTimes.get(listTimes.size() - 1).getDate().getTime());
            long diffInMinutes = diffInMilliseconds / (60 * 1000);
            if (diffInMinutes < 15) {
                System.out.println("Wait 15 min, and try again");
                return false;
            }
            return true;
        }
        return true;
    }


    public boolean loginSuccess(String userName, String pass){
        try {
            Password password = new Password();

            //Check if there is a user with this name.

            Query userQuery = entityManager.createQuery("SELECT p FROM EntityUserInfo p WHERE p.nickname = ?1", EntityUserInfo.class);
            userQuery.setParameter(1,userName );
            List<EntityUserInfo> userList = userQuery.getResultList();
            if (userList.isEmpty()) return false;

            //Check if this password is correct to this user id from the user table.

            Query passwordQuery = entityManager.createQuery("SELECT p FROM EntityUserPassword p WHERE p.userId = ?1", EntityUserPassword.class);
            passwordQuery.setParameter(1, userList.get(0).getId());
            List<EntityUserPassword> passwordList = passwordQuery.getResultList();
            if (passwordList.get(0).getPassword().equals(pass)) {
                int id = password.get_user_id(userName);
                if (get_correct_time(id, passwordList.get(0).getPassword()))
                    return Authenticator.passwordTotp(userList.get(0).getSecretKey(), new Scanner(System.in).nextLine());
            }
            return false;

        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
