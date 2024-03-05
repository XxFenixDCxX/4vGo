package es.cuatrovientos.a4vgo.activities.fakeData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFakeDataDB {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference usersRef = db.collection("personalDetails");

    public static void addFakeUsersToFirebase(int numUsers) {
        List<Map<String, Object>> userProfileList = createFakeUserList(numUsers);

        for (Map<String, Object> userProfile : userProfileList) {
            usersRef.add(userProfile)
                    .addOnSuccessListener(documentReference -> {String userId = documentReference.getId();
                        updateCo2Points(userId);
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }
    }

    private static void updateCo2Points(String userId) {

    }

    private static List<Map<String, Object>> createFakeUserList(int numUsers) {
        List<Map<String, Object>> userList = new ArrayList<>();

        for (int i = 0; i < numUsers; i++) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("firstName", "FirstName" + i);
            userMap.put("lastName", "LastName" + i);
            userMap.put("phoneNumber", "PhoneNumber" + i);
            userMap.put("birthdate", "12/04/2002");
            userMap.put("dni", "12345-" + i);
            userMap.put("email", "prueba" + i + "@gmail.com");
            userMap.put("language", "es");
            userMap.put("name", "Name" + i);
            userMap.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/v-go-6ce57.appspot.com/o/profile_images%2F12345678Z.jpg?alt=media&token=724d9483-5626-4e8f-8b9b-dc4bbe1d95b6");
            userMap.put("spam", false);
            userMap.put("surname", "Bensbaho" + i);
            userMap.put("username", "xxstux" + i);
            userMap.put("co2Points", 0.0 + i);

            userList.add(userMap);
        }

        return userList;
    }

}
