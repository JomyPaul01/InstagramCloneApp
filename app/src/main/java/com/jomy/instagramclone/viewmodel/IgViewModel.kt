package com.jomy.instagramclone.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jomy.instagramclone.IgApplication
import com.jomy.instagramclone.R
import com.jomy.instagramclone.data.Event
import com.jomy.instagramclone.data.PostData
import com.jomy.instagramclone.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class IgViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val application:IgApplication
) : ViewModel() {

    private val stringResourceProvider = StringResourceProvider(application.applicationContext)
    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val popUpNotification = mutableStateOf<Event<String>?>(null)

    val refreshPostProgress = mutableStateOf(false)
    val posts = mutableStateOf<List<PostData>>(listOf())

    val searchProgress = mutableStateOf(false)
    val searchedPosts = mutableStateOf<List<PostData>>(listOf())

    val postFeedProgress = mutableStateOf(false)
    val postsFeed = mutableStateOf<List<PostData>>(listOf())


    init {

        auth.currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun onSignUp(username: String, email: String, password: String) {
        if (username.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = (stringResourceProvider.getString(R.string.please_fill_in_all_the_fields)))
            return
        }
        inProgress.value = true
        db.collection(Constants.USERS).whereEqualTo(Constants.USERNAME, username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    handleException(customMessage = stringResourceProvider.getString(R.string.username_already_exist))
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                val userData = UserData(name = username, userName = email)
                                createOrUpdateProfile(userData)
                            } else {
                                handleException(task.exception, customMessage = stringResourceProvider.getString(
                                    R.string.sign_up_failed
                                ))
                            }
                        }
                }
                inProgress.value = false
            }
            .addOnFailureListener {

            }
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = stringResourceProvider.getString(R.string.please_enter_all_the_fields))
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                inProgress.value = false
                signedIn.value = true
                auth.currentUser?.uid?.let { uid ->
                    getUserData(uid)
                }
            }
            .addOnFailureListener { exc ->
                inProgress.value = false
                handleException(exc, stringResourceProvider.getString(R.string.login_failed))
            }
    }

    private fun createOrUpdateProfile(userData: UserData) {
        val uid = auth.currentUser?.uid
        uid?.let { uid ->
            userData.userId = uid
            inProgress.value = true
            db.collection(Constants.USERS).document(uid).get().addOnSuccessListener { it ->
                if (it.exists()) {
                    it.reference.update(userData.toMap()).addOnSuccessListener {
                        inProgress.value = false
                        this.userData.value = userData
                    }.addOnFailureListener {
                        inProgress.value = false
                        handleException(it,
                            stringResourceProvider.getString(R.string.cannot_update_user))
                    }
                } else {
                    db.collection(Constants.USERS).document(uid).set(userData)
                        .addOnCompleteListener {
                            inProgress.value = false
                            getUserData(uid)
                        }


                }
            }.addOnFailureListener {
                inProgress.value = false
                handleException(it,
                    stringResourceProvider.getString(R.string.could_not_create_user_profile))
            }

        }

    }

    private fun getUserData(uid: String) {
        db.collection(Constants.USERS).document(uid).get().addOnSuccessListener {
            val user = it.toObject<UserData>()
            userData.value = user
            inProgress.value = false
            refreshPosts()
            getPersonalisedFeed()
        }
            .addOnFailureListener { exc ->
                handleException(exc, stringResourceProvider.getString(R.string.cannot_retrieve_user_data))
                inProgress.value = false
            }
    }

    private fun handleException(exception: Exception? = null, customMessage: String) {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage : $errorMsg"
        popUpNotification.value = Event(message)

    }

    fun updateProfileData(userData: UserData) {
        createOrUpdateProfile(userData)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("${Constants.IMAGES}/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }.addOnFailureListener { exc ->
            handleException(exc,
                stringResourceProvider.getString(R.string.upload_profile_photo_failed))
        }

    }

    fun uploadProfile(uri: Uri) {
        uploadImage(uri) {
            userData.value?.imageUrl = it.toString()
            userData.value?.let { it1 -> createOrUpdateProfile(userData = it1) }
        }
    }

    fun onLogOut() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popUpNotification.value = Event(stringResourceProvider.getString(R.string.logged_out))
        searchedPosts.value = listOf()
        postsFeed.value = listOf()
    }

    fun onNewPost(imageUri: Uri, description: String?, onPostSuccess: () -> Unit) {

        val filterWords = listOf("it", "the", "be", "to", "is", "of", "and", "or", "a", "in")
        val searchTerms = description?.split(" ", ",", ".", "?", "!", "#")?.map {
            it.lowercase(
                Locale.ROOT
            )
        }
            ?.filter { it.isNotEmpty() and !filterWords.contains(it) }

        uploadImage(imageUri) {
            val uuid = UUID.randomUUID()
            val post = PostData(
                uuid.toString(),
                auth.currentUser?.uid!!,
                userData.value?.userName,
                userData.value?.imageUrl,
                it.toString(),
                description,
                System.currentTimeMillis(),
                likes = listOf(),
                searchTerms = searchTerms
            )
            createPost(post, onPostSuccess)
        }
    }

    private fun createPost(post: PostData, onPostSuccess: () -> Unit) {
        inProgress.value = true
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            val docRef = db.collection(Constants.POSTS).document()
            post.postId = docRef.id
            docRef.set(post)
                .addOnSuccessListener {
                    popUpNotification.value = Event(stringResourceProvider.getString(R.string.post_created_successfully))
                    inProgress.value = false
                    refreshPosts()
                    onPostSuccess.invoke()

                }
                .addOnFailureListener { exc ->
                    handleException(exc,
                        stringResourceProvider.getString(R.string.unable_to_create_post))
                    inProgress.value = false
                }

        } else {
            handleException(customMessage = stringResourceProvider.getString(R.string.error_creating_post_user_not_found))
            onLogOut()
            inProgress.value = false
        }
    }

    private fun refreshPosts() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            refreshPostProgress.value = true
            db.collection(Constants.POSTS).whereEqualTo("userId", currentUserId).get()
                .addOnSuccessListener { document ->
                    convertPost(document, posts)
                    refreshPostProgress.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(exc, customMessage = stringResourceProvider.getString(R.string.unable_to_refresh_post))
                    refreshPostProgress.value = false
                }
        } else {
            handleException(customMessage = stringResourceProvider.getString(R.string.user_not_found_hence_unabel_to_fetch_post))
            onLogOut()
            refreshPostProgress.value = false
        }

    }

    private fun convertPost(document: QuerySnapshot?, posts: MutableState<List<PostData>>) {

        val newPosts = mutableListOf<PostData>()
        document?.forEach { doc ->
            val post = doc.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPost = newPosts.sortedByDescending { it.time }
        posts.value = sortedPost

    }

    fun searchPost(searchTerm: String) {
        if (searchTerm.isEmpty())
            return
        searchProgress.value = true
        db.collection(Constants.POSTS)
            .whereArrayContains("searchTerms", searchTerm.trim().lowercase())
            .get()
            .addOnSuccessListener {
                convertPost(it, searchedPosts)
                searchProgress.value = false
            }
            .addOnFailureListener { exc ->
                handleException(exc,
                    stringResourceProvider.getString(R.string.search_not_successful))
                searchProgress.value = false
            }
    }

    fun onFollowCick(userId: String) {
        auth.currentUser?.uid?.let { currentUser ->
            val following = arrayListOf<String>()
            userData.value?.following?.let {
                following.addAll(it)
                if (following.contains(userId)) {
                    following.remove(userId)
                } else {
                    following.add(userId)
                }
                db.collection(Constants.USERS).document(currentUser).update(stringResourceProvider.getString(R.string.following), following)
                    .addOnSuccessListener { getUserData(currentUser) }
            }
        }
    }

     fun getPersonalisedFeed() {
        val following = userData.value?.following
        if (!following.isNullOrEmpty()) {
            postFeedProgress.value = true

            db.collection(Constants.POSTS).whereIn("userId", following).get()
                .addOnSuccessListener {
                    convertPost(it, postsFeed)
                    if (postsFeed.value.isEmpty()) {
                        getGeneralFeed()
                    } else {
                        postFeedProgress.value = false
                    }
                }
                .addOnFailureListener {
                    postFeedProgress.value = false
                    getGeneralFeed()
                }

        }
        getGeneralFeed()

    }

    private fun getGeneralFeed() {
        postFeedProgress.value = true
       // val currentTimeMillis = System.currentTimeMillis()
        //val difference = 24 * 60 * 60 * 1000
        val difference = 0
        val currentTimeMillis = 0
        db.collection(Constants.POSTS)
            .whereGreaterThan(stringResourceProvider.getString(R.string.time), currentTimeMillis - difference).get()
            .addOnSuccessListener {
                postFeedProgress.value = false
                convertPost(it, postsFeed)
            }
            .addOnFailureListener { exc ->
                handleException(exc, stringResourceProvider.getString(R.string.failed_to_fetch_feed))
                postFeedProgress.value = false
            }
    }

    fun onLikePost(postData: PostData) {
        auth.currentUser?.uid?.let { userId ->
            val newLikes = arrayListOf<String>()
            if (postData.likes == null) {
                newLikes.add(userId)
            }
            postData.likes?.let { likes ->
                if (likes.contains(userId)) {
                    newLikes.addAll(likes.filter { userId != it })
                } else {
                    newLikes.addAll(likes)
                    newLikes.add(userId)
                }
            }
            postData.postId?.let {postId->
                db.collection(Constants.POSTS).document(postId).update(
                    stringResourceProvider.getString(
                        R.string.likes
                    ), newLikes)
                    .addOnSuccessListener {
                        postData.likes = newLikes
                    }
                    .addOnFailureListener {
                        handleException(it, stringResourceProvider.getString(R.string.unable_to_like_post))
                    }
            }

        }
    }


}