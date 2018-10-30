## Build an example app using Sync API and Persistence Library with Contentstack’s Android SDK
This is an example app built using Contentstack’s Android SDK, Sync API, and Persistence Library. You can try out and play with our Sync API and data persistence with this example app, before building bigger and better applications.

The [Persistence Library](https://www.contentstack.com/docs/guide/synchronization/using-realm-persistence-library-with-android-sync-sdk) lets you store data on the device’s local storage, helping you create apps that can work offline too.  Perform the steps given below to use the app.

Perform the steps given below to use this app.

### Prerequisites
* [Android Studio]()
* [Contentstack account](https://app.contentstack.com/#!/login)
* [Basic knowledge of Contentstack](https://www.contentstack.com/docs/)

In this tutorial, we will first go through the steps involved in configuring Contentstack and then look at the steps required to customize and use the presentation layer.

#### Step 1: Create a stack
Log in to your Contentstack account, and [create a new stack](https://www.contentstack.com/docs/guide/stack#create-a-new-stack). Read more about [stack](https://www.contentstack.com/docs/guide/stack).
#### Step 2: Add a publishing environment
[Add a publishing environment](https://www.contentstack.com/docs/guide/environments#add-an-environment) to publish your content in Contentstack. Provide the necessary details as per your requirement. Read more about [environments](https://www.contentstack.com/docs/guide/environments).
#### Step 3: Import content types
For this app, we need just one content type: Session. Here’s what it’s needed for:

- Session: Lets you add the session content to your app

For quick integration, we have already created the content type. [Download the content type](https://drive.google.com/open?id=1jnpNIHRb4kNcP3r0I2QMatGzPwNXxpzS
) and [import](https://www.contentstack.com/docs/guide/content-types#importing-a-content-type) it to your stack. (If needed, you can [create your own content types](https://www.contentstack.com/docs/guide/content-types#creating-a-content-type). Read more about [Content Types](https://www.contentstack.com/docs/guide/content-types)).

Now that all the content types are ready, let’s add some content for your Stack.

#### Step 4: Adding content
[Create](https://www.contentstack.com/docs/guide/content-management#add-a-new-entry) and [publish](https://www.contentstack.com/docs/guide/content-management#publish-an-entry) entries for the ‘Session’ content type.

Now that we have created the sample data, it’s time to use and configure the presentation layer.



#### Step 5: Clone and configure the application
To get your app up and running quickly, we have created a sample app. Clone the Github repo given below and change the configuration as per your need:

```
$ git clone https://github.com/contentstack/contentstack-android-persistence-example.git
```

Now add your Contentstack API Key, Delivery Token, and Environment to the project during the SDK initialization step. (Find your [Stack's API Key and Delivery Token](https://www.contentstack.com/docs/apis/content-delivery-api/#authentication
).)

```
Stack stack = Contentstack.stack(context,“api_key”, “delivery_token”, “environment");
```

This will initiate your project.

#### Step 6: Install and set up Realm
We need to first download Realm and install it. To do so, perform the steps given below:

- Add the latest version of [Realm](https://realm.io/docs/java/latest) library in your project and follow [Installation](https://realm.io/docs/java/latest#installation) gulde to complete setup.

#### Step 7: Install Contentstack Android SDK and SyncManager

Now that your Realm installation is ready, let's look at the steps involved in setting up your Contentstack SDK.

1. Download and set up the Contentstack android SDK. Read the [Contentstack android SDK Documentation]([https://www.contentstack.com/docs/platforms/android](https://www.contentstack.com/docs/platforms/android)) for more details.

2. You will find the "syncwrapper" folder, which contains the following four files:
   - SyncManager
   - RealmPersistenceHelper
   - SyncStore
   - DbQuery

3. Add the "syncwrapper" folder to your src folder in project.


#### Step 8: Map data


There are three important items to be mapped in our Synchronization process:

- Sync token/pagination token
- Entries Mapping
- Assets  Mapping

Let’s look at how to persist Sync Token & Pagination Token

#### Sync token/pagination token

To save Sync Token and Pagination Token, we need SyncStore class file  which will manage the storage and retrieval of updated Sync Token and Pagination Token.

```
if (stackResponse.getPaginationToken()!=null){
   persistsToken(stackStack.getPaginationToken());
}else{
   persistsToken(stackStack.getSyncToken());
}
```

#### Entry Mapping

To begin with, let’s consider an example of our Example app. Let’s say we have  content type: Session.
Let’s see how to implement this example.

Create a table class named Session extending RealmObject, and add following code to implement EntityFields as shown below:

```
// @RealmClass accepts ("name= "content_type_uid"")
@RealmClass(name = "session")
public class Session extends RealmObject {

   *************************************
   // Mandatory fields
   @PrimaryKey
   @RealmField(name = "uid")
   private String uid;
   *************************************

//Note: the annotation name will be content_type's field id
   @RealmField(name="title")
//user defined fields may be anything of user liking.
   private String mTitle;

   @RealmField(name="is_popular")
   private String mIsPopular;

   @RealmField(name="type")
   private String mType;

   @RealmField(name="session_id")
   private String mId;

   @RealmField(name="tags")
   private String mTags;

   @RealmField(name="locale")
   private String mLocale;

   @RealmField(name="speakers")
   private RealmList<Speaker> speaker;

   @RealmField(name="track")
   private String mTrack;

   @RealmField(name="start_time")
   private String mStartTime;

   @RealmField(name="end_time")
   private String mEndTime;

   @RealmField(name="room")
   private Room mRoom;

  ... Generated getters and setters ...

   }
   ```

You also need to implement the fieldMapping function which returns the mapping of attributes and entry fields in Contentstack.

Similarly, we can add other entries and mapping for each entry.

#### Asset Map

To map Assets, you need to create a table for assets named SysAssets and extend RealmObject. Add the following code to implement AssetProtocol.

```
@RealmClass(name="sys_assets")
public class SysAssets extends RealmObject {

******************************
   // Mandatory fields
   @PrimaryKey
   @RealmField(name = "uid")
   private String uid;
******************************

 // Note: the annotation name will be content_type's field id
   @RealmField(name="created_at")
 //user defined fields may be anything of user liking.
   private String created_at;

   @RealmField(name = "updated_at")
   private String updated_at;
   @RealmField(name = "created_by")
   private String created_by;
   @RealmField(name = "updated_by")
   private String updated_by;
   @RealmField(name = "content_type")
   private String content_type;
   @RealmField(name = "file_size")
   private String file_size;
   @RealmField(name = "tags")
   private String tags;
   @RealmField(name = "filename")
   private String filename;
   @RealmField(name = "url")
   private String url;
   @RealmField(name = "is_dir")
   private String is_dir;
   @RealmField(name = "parent_uid")
   private String parent_uid;
   @RealmField(name = "_version")
   private String version;
   @RealmField(name = "title")
   private String title;
   @RealmField(name = "publish_details")
   private String publish_details;

   ... Generated getters and setters ...

   }
```

Now, our final step is to initiate SyncManager and begin with the Sync content type.

### Step 9: Initiate SyncManager and Sync
Finally, after setting up the content mapping, initiate SyncManager. It takes Stack instance and RealmPersistenceHelper class instance as follows:

```
//Get stack instance like below
Stack stack = Contentstack.stack(context, "api_key", "access_token", "environment");

//Get realm instance like below
Realm realmInstance = Realm.getDefaultInstance();
Get realmPersistenceHelper instance like
RealmStore realmStore = new RealmStore(realmInstance);
SyncManager syncManager = new SyncManager(realmStore, stack);
syncManager.stackRequest();


```
Screenshot

<img src="https://github.com/contentstack/contentstack-android-persistence-example/blob/master/app/src/main/assets/image/example.png"  height="500" width="280">



### More Resources
* [Getting started with Android SDK](https://www.contentstack.com/docs/platforms/android)
* [Using the Sync API with Android SDK](https://www.contentstack.com/docs/guide/synchronization/using-the-sync-api-with-android-sdk)
* [Using Persistence Library with Android SDK](https://www.contentstack.com/docs/guide/synchronization/using-realm-persistence-library-with-android-sync-sdk)
* [Sync API documentation](https://www.contentstack.com/docs/apis/content-delivery-api/#synchronization)

