## Build an example app using Sync API and Persistence Library with Contentstack’s Android SDK
This is an example app built using Contentstack’s Android SDK, Sync API, and Persistence Library. You can try out and play with our Sync API and data persistence with this example app, before building bigger and better applications.

The [Persistence Library](https://www.contentstack.com/docs/guide/synchronization/using-realm-persistence-library-with-android-sync-sdk) lets you store data on the device’s local storage, helping you create apps that can work offline too.  Perform the steps given below to use the app.

Perform the steps given below to use this app.

### Prerequisites
* [Android Studio]()
* [Contentstack account](https://app.contentstack.com/#!/login)
* [Basic knowledge of Contentstack](https://www.contentstack.com/docs/)

In this tutorial, we will first go through the steps involved in configuring Contentstack and then look at the steps required to customize and use the presentation layer.

### Step 1: Create a stack
Log in to your Contentstack account, and [create a new stack](https://www.contentstack.com/docs/guide/stack#create-a-new-stack). Read more about [stacks](https://www.contentstack.com/docs/guide/stack).
### Step 2: Add a publishing environment
[Add a publishing environment](https://www.contentstack.com/docs/guide/environments#add-an-environment) to publish your content in Contentstack. Provide the necessary details as per your requirement. Read more about [environments](https://www.contentstack.com/docs/guide/environments).
### Step 3: Import content types
For this app, we need just one content type: Session. Here’s what it’s needed for:

- Session: Lets you add the session content to your app

For quick integration, we have already created the content type. [Download the content type](https://drive.google.com/open?id=1jnpNIHRb4kNcP3r0I2QMatGzPwNXxpzS
) and [import](https://www.contentstack.com/docs/guide/content-types#importing-a-content-type) it to your stack. (If needed, you can [create your own content types](https://www.contentstack.com/docs/guide/content-types#creating-a-content-type). Read more about [Content Types](https://www.contentstack.com/docs/guide/content-types)).

Now that all the content types are ready, let’s add some content for your sync playground app.

### Step 4: Adding content
[Create](https://www.contentstack.com/docs/guide/content-management#add-a-new-entry) and [publish](https://www.contentstack.com/docs/guide/content-management#publish-an-entry) entries for the ‘Session’ content type.

Now that we have created the sample data, it’s time to use and configure the presentation layer.
### Step 5: Set up and initialize Android SDK
To set up and initialize Contentstack’s Android SDK, refer to our documentation [here](https://www.contentstack.com/docs/platforms/android#getting-started).
### Step 6: Clone and configure the application
To get your app up and running quickly, we have created a sample app. Clone the Github repo given below and change the configuration as per your need:

$ git clone [https://github.com/contentstack/contentstack-android-persistence-example.git](https://github.com/contentstack/contentstack-android-persistence-example.git)

Now add your Contentstack API Key, Delivery Token, and Environment to the project during the SDK initialization step. (Find your [Stack's API Key and Delivery Token](https://www.contentstack.com/docs/apis/content-delivery-api/#authentication
).)

```
Config config = new Config();
config.setHost(BuildConfig.STACK_URL);
Stack stack = Contentstack.stack(getApplicationContext(), “api_key”, “delivery_token”, “environment”, config);
```

This will initiate your project.

### Step 7: Map data

 In order to sync this data with Realm, we need to add data mappings. The three important items to be mapped in our Synchronization process are as follows:

* Sync token/Pagination token
* Entries
* Assets

Let’s look at how each of the above can be mapped.

### Sync Token Mapping
To save Sync-Token and Pagination-Token, we need the SyncStore table which will manage the storage and retrieval of updated sync token and pagination token.

```
if (stackResponse.getPaginationToken()!=null){
   persistsToken(stackResponse.getPaginationToken());
}else {
   persistsToken(stackResponse.getSyncToken());
}
```

### Entry Mapping
To begin with, let’s consider an example of our Example app. Let’s say we have two content types: Session and Speaker. And, the ‘Session’ content type has a reference field that refers to multiple entries of the Speaker content type. Let’s see how to implement this example.

Create a table class named Session extending RealmObject, and add following code to implement EntityFields as shown below:

```
'@RealmClass(name = "session")
public class Session extends RealmObject {
   // Mandatory fields
   @PrimaryKey
   @RealmField(name = "uid")
   private String uid;
   // user defined fields
   @RealmField(name = "title")
   private String mTitle;
   @RealmField(name = "is_popular")
   private String mIsPopular;
   @RealmField(name = "type")
   private String mType;
   @RealmField(name = "session_id")
   private String mId;
   @RealmField(name = "tags")
   private String mTags;
   @RealmField(name = "locale")
   private String mLocale;
   @RealmField(name = "speakers")
   private RealmList<Speaker> speaker;
   @RealmField(name = "track")
   private String mTrack;
   @RealmField(name = "start_time")
   private String mStartTime;
   @RealmField(name = "end_time")
   private String mEndTime;
   @RealmField(name = "room")
   private Room mRoom;
   // provide getter setter for fields
   }
   ```

You also need to implement the fieldMapping function which returns the mapping of attributes and entry fields in Contentstack.

Similarly, we can add other entity and mapping for each entity.
Asset Mapping
To map Assets, you need to create a table for assets named SysAssets and extend RealmObject. Add the following code to implement AssetProtocol.

```
@RealmClass(name = "sys_assets")
public class SysAssets extends RealmObject {
   // Mandatory fields
   @PrimaryKey
   @RealmField(name = "uid")
   private String uid;
   // user defined fields
   @RealmField(name = "created_at")
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
   // Provide getter & setters for the class
   }
```

Now, our final step is to initiate SyncManager and begin with the Sync content type.

### Step 8: Initiate SyncManager and Sync
Finally, after setting up the content mapping, initiate SyncManager. It takes Stack instance and Helper class instance as follows:

```
//Get stack instance like below
Stack stack = Contentstack.stack(context, "api_key", "access_token", "environment");

//Get realm instance like below
Realm realmInstance = Realm.getDefaultInstance();
Get helper instance like
Helper helper = new Helper(realmInstance)
SyncManager manager = new SyncManager(helper, stack);
manager.stackRequest()
```
Screenshot

<img src="https://github.com/contentstack/contentstack-android-persistence-example/blob/master/app/src/main/assets/image/example.png"  height="600" width="300">



### More Resources
* [Getting started with Android SDK](https://www.contentstack.com/docs/platforms/android)
* [Using the Sync API with Android SDK](https://www.contentstack.com/docs/guide/synchronization/using-the-sync-api-with-android-sdk)
* [Using Persistence Library with Android SDK](https://www.contentstack.com/docs/guide/synchronization/using-realm-persistence-library-with-android-sync-sdk)
* [Sync API documentation](https://www.contentstack.com/docs/apis/content-delivery-api/#synchronization)





