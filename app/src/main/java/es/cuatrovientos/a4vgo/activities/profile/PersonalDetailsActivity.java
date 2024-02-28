package es.cuatrovientos.a4vgo.activities.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.Utils.DialogUtils;
public class PersonalDetailsActivity extends AppCompatActivity {
    Query query;
    CollectionReference collection;
    FirebaseUser currentUser;
    TextView username;
    EditText name, surname, email, eTxtUsername;
    ImageButton back;
    ImageView profile;
    Button save;
    String currentUserDNI;
    Drawable originalDrawable;
    Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        collection = FirebaseFirestore.getInstance().collection("personalDetails");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        query = collection.whereEqualTo("email", currentUser.getEmail());
        username = findViewById(R.id.txtForUsername);
        name = findViewById(R.id.txtNamePersonalDetails);
        surname = findViewById(R.id.txtSurnamePersonalDetails);
        email = findViewById(R.id.txtEmailPersonalDetails);
        eTxtUsername = findViewById(R.id.txtUsernamePersonalDetails);
        back = findViewById(R.id.btnBackPersonalDetails);
        save = findViewById(R.id.btnSave);
        profile = findViewById(R.id.imgProfileP);
        originalDrawable = profile.getBackground();

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                String usernameString = document.getString("username");
                currentUserDNI = document.getString("dni");
                assert usernameString != null;
                usernameString = usernameString.toUpperCase().charAt(0) + usernameString.substring(1);
                username.setText(usernameString);
                email.setText(document.getString("email"));
                eTxtUsername.setText(usernameString);
                surname.setText(document.getString("surname"));
                name.setText(document.getString("name"));
                loadProfileImage();
            } else {
                DialogUtils.showErrorDialog(this, getString(R.string.errorLogCantLoadDetails), getString(R.string.errorLoginMessage));
            }
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalDetailsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        save.setOnClickListener(v -> {
            //toDo Realizar el almacenaje en la base de datos con las validaciones
            if (selectedImageUri != null) {
                // Perform the image upload and database update
                uploadImageToFirebaseStorage(selectedImageUri);
            }
        });

        profile.setOnClickListener(v -> {
            profile.setBackground(originalDrawable);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.selectImage)), PICK_IMAGE_REQUEST);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profile.setBackground(null);
                profile.setBackground(new BitmapDrawable(getResources(), bitmap));

            } catch (IOException e) {
                e.printStackTrace();
                DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogImage));
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images/" + currentUserDNI + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(this::updateProfileImage))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogImage));
                });
    }
    private void updateProfileImage(Uri imageUrl) {
        collection.document(currentUserDNI)
                .update("profileImage", imageUrl.toString());
    }

    private void loadProfileImage() {
        collection.document(currentUserDNI).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("profileImage");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // If profile image URL is available, download it and set as background
                            downloadImageAndSetBackground(imageUrl);
                        } else {
                            // If no profile image URL, set the default image
                            profile.setBackgroundResource(R.drawable.ic_profile);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogImage));
                });
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadImageAndSetBackground(String imageUrl) {
        new AsyncTask<Void, Void, Drawable>() {
            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    // Download the image as a Drawable
                    InputStream inputStream = new URL(imageUrl).openStream();
                    return Drawable.createFromStream(inputStream, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                if (drawable != null) {
                    // Set the downloaded image as the background
                    profile.setBackground(drawable);
                } else {
                    // Set the default image
                    profile.setBackgroundResource(R.drawable.ic_profile);
                }
            }
        }.execute();
    }
}