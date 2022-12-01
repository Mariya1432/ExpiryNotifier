package com.example.expirynotifier;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class FirebaseUtils {


    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final StorageReference profilePhotoStorage = firebaseStorage.getReference().child("profile");
    private final StorageReference notesPhotoStorage = firebaseStorage.getReference().child("reminderImages");


    //Firebase Auth
    public Task<AuthResult> register(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }


    public Task<Void> updateProfile(String name, String uri) {
        UserProfileChangeRequest.Builder userProfileChangeRequest =
                new UserProfileChangeRequest.Builder();
        if (name != null) {
            userProfileChangeRequest.setDisplayName(name);
        }
        if (uri != null) {
            userProfileChangeRequest.setPhotoUri(Uri.parse(uri));
        }
        return getCurrentUser().updateProfile(userProfileChangeRequest.build());
    }


    //Firebase Firestore
    public Task<Void> addNewReminder(ReminderClass reminderClass, String uuid) {
        return firestore.collection(getCurrentUser().getUid()).document(uuid).set(reminderClass);
    }

    public CollectionReference getAllReminder() {
        if (getCurrentUser() == null) {
            return null;
        }
        return firestore.collection(getCurrentUser().getUid());
    }

    public DocumentReference getSpecificReminder(String uuid) {
        if (getCurrentUser() == null) {
            return null;
        }
        return firestore.collection(getCurrentUser().getUid()).document(uuid);
    }

    public Task<Void> deleteReminder(String uuid) {
        if (getCurrentUser() == null) {
            return null;
        }
        return firestore.collection(getCurrentUser().getUid()).document(uuid).delete();
    }


    //Firebase Storage
    public UploadTask uploadImage(Uri imageUri) {
        return profilePhotoStorage.child(getCurrentUser().getUid()).putFile(imageUri);
    }

    public UploadTask uploadReminderImage(Uri imageUri, String fileName) {
        return notesPhotoStorage.child(getCurrentUser().getUid()).child(fileName).putFile(imageUri);
    }

    public StorageReference getReminderImageLocation(String fileName) {
        return notesPhotoStorage.child(getCurrentUser().getUid()).child(fileName);
    }

    public Task<Void> deleteImageWithUrl(String url) {
        return firebaseStorage.getReferenceFromUrl(url).delete();
    }

    public void deleteImageWithReference(StorageReference storageReference) {
        storageReference.delete();
    }

    public StorageReference getProfileStorageReference() {
        return profilePhotoStorage.child(getCurrentUser().getUid());
    }


}
