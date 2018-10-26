package com.contentstack.persistance.manager;

import android.util.ArrayMap;
import android.util.Log;

import com.contentstack.persistance.view.SessionActivity;
import com.contentstack.persistance.view.Splash;
import com.contentstack.persistance.view.SyncListener;
import com.contentstack.sdk.Error;
import com.contentstack.sdk.Stack;
import com.contentstack.sdk.SyncResultCallBack;
import com.contentstack.sdk.SyncStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.annotations.RealmField;


public class SyncManager {

    private Stack stackInstance;

    private RealmPersistenceHelper realmPersistenceHelperInstance;

    private Realm realmInstance;

    private ArrayMap<String, Class> CONTENT_TYPE_CLASS_MAPPER = new ArrayMap<String, Class>();

    private ArrayMap<Class, ArrayMap<String, String>> CLASS_FIELDS_MAPPER = new ArrayMap<Class, ArrayMap<String, String>>();

    private String TAG = SyncManager.class.getSimpleName();

    private SyncListener syncListener;



    /**
     * SyncManager Constructor to initialise the Sync Manager
     * and get @{@link RealmPersistenceHelper} and @{@link Stack} class instance
     * @param realmPersistenceHelper @{@link RealmPersistenceHelper} instance
     * @param stackInstance @{@link Stack} instance
     */
    public SyncManager(RealmPersistenceHelper realmPersistenceHelper, Stack stackInstance) {

        this.realmPersistenceHelperInstance = realmPersistenceHelper;
        this.stackInstance  = stackInstance;
        realmInstance = realmPersistenceHelperInstance.getRealmInstance();
        setClassMapping();
    }


    public void setViewListener(SessionActivity session) {
        syncListener = session;
    }

    public void setViewListener(Splash splash) {
        syncListener = splash;
    }

    /**
     * It finds stored sync_token for next api call
     * @return sync token @{@link String}
     */
    private String getSyncToken(){

        SyncStore tokenStore = realmInstance.where(SyncStore.class).findFirst();
        if (tokenStore !=null ){
            return realmInstance.where(SyncStore.class).findFirst().getSync_token();
        }else {
            return null;
        }

    }


    private String getPaginationToken(){
        SyncStore tokenStore = realmInstance.where(SyncStore.class).findFirst();
        if (tokenStore !=null ){
            return realmInstance.where(SyncStore.class).findFirst().getPagination_token();
        }else {
            return null;
        }
    }



    /**
     * It provides sync request to create or update data in the local database system
     */
    public void stackRequest()    {


        if (getSyncToken()!= null){

            stackInstance.syncToken(getSyncToken(), new SyncResultCallBack() {
                @Override
                public void onCompletion(SyncStack syncStack, Error error) {
                    if (error == null){
                        syncListener.onSuccessListener();
                        parseResponse(syncStack);
                    }
                }
            });

        }else if (getPaginationToken()!= null){

            stackInstance.syncPaginationToken(getPaginationToken(),  new SyncResultCallBack() {
                @Override
                public void onCompletion(SyncStack syncStack, Error error) {
                    if (error == null){
                        syncListener.onSuccessListener();
                        parseResponse(syncStack);
                    }
                }
            });

        }else {

            stackInstance.sync(new SyncResultCallBack() {
                @Override
                public void onCompletion(SyncStack syncStack, Error error) {
                    if (error == null) {
                        syncListener.onSuccessListener();
                        parseResponse(syncStack);
                    }else {
                        syncListener.onMessage(error.getErrorMessage());
                        Log.e(TAG,error.getErrorMessage());
                    }
                }
            });
        }

    }




    /**
     * SyncStack Response that contains other function from @{@link SyncStack}
     * GetSyncToken() Limit etc...
     * @param stackResponse
     * parse JSON response and updated sync_token every time server results
     */
    private void parseResponse(SyncStack stackResponse) {

        ArrayList<JSONObject> jsonList = stackResponse.getItems();
        String syncToken = stackResponse.getSyncToken();
        String pagiToken = stackResponse.getPaginationToken();
        if (syncToken!=null){
            persistsToken(syncToken, pagiToken);
        }
        jsonList.forEach(item -> handleJSON(item));
    }




    /**
     * Takes a single Json object for processing the keys and values and insert in table on the
     * basis of class and content_type_uid and delete the items form the table.
     * @param item
     */
    private void handleJSON(JSONObject item) {

        String content_type = item.optString("content_type_uid");
        boolean containsKEY = CONTENT_TYPE_CLASS_MAPPER.containsKey(content_type);
        if (containsKEY){

            Class modelClass = CONTENT_TYPE_CLASS_MAPPER.get(content_type);
            if (item.has("data")){

                String publishType  = item.optString("type");
                JSONObject resultObject =item.optJSONObject("data");
                String uid =item.optJSONObject("data").optString("uid");

                if (publishType.equalsIgnoreCase("entry_published") || publishType.equalsIgnoreCase("asset_published")){

                    JSONArray mapJSON = new JSONArray();
                    mapJSON.put(jsonDECODER(modelClass, resultObject));
                    realmPersistenceHelperInstance.findOrCreate(modelClass, uid, mapJSON);

                }else if (publishType.equalsIgnoreCase("entry_unpublished")
                        || publishType.equalsIgnoreCase("entry_deleted")
                        || publishType.equalsIgnoreCase("asset_unpublished")
                        || publishType.equalsIgnoreCase("asset_deleted")){

                    if (item.has("data")){
                        realmPersistenceHelperInstance.deleteRow(modelClass, uid);
                    }
                }else if (publishType.equalsIgnoreCase("content_type_deleted")){
                    realmPersistenceHelperInstance.deleteTable(modelClass);
                }
            }
        }
    }




    /**
     * @param tableClass returns Table Model class name , It receives table name by Annotation created over class name.
     * @param resultObject json object for the position in Json array result.
     */
    private JSONObject jsonDECODER(Class tableClass, JSONObject resultObject) {

        JSONObject resultObj = new JSONObject();
        ArrayMap<String, String> fieldMap = CLASS_FIELDS_MAPPER.get(tableClass);

        Iterator keyValueIterator = null;
        if (fieldMap != null && fieldMap.size()>0) {
            keyValueIterator = fieldMap.entrySet().iterator();
        }

        while(keyValueIterator.hasNext()) {
            Map.Entry keyValueEntry = (Map.Entry)keyValueIterator.next();
            String keyString = (String)keyValueEntry.getKey();
            if (resultObject.has(keyString)){
                resultObj = getJsonTypeToParse(tableClass, resultObj, resultObject, keyValueEntry, keyString);
            }
        }

        return resultObj;
    }




    private void checkForGroupAvailableToDelete(Class tableClass, String UID) {
        Log.e("In Group Selection Deletion Dept", tableClass+" uid"+UID);
        Object abcjd = realmInstance.where(tableClass).equalTo("uid", UID).contains("", "").findFirst();
    }



    /**
     *
     * @param tableClass @{@link Class<? extends io.realm.RealmObject>}
     * @param reqObj Its is simple JSON which results a json to table  @{@link JSONObject}
     * @param resultObject Its is Single Json Item for processing @{@link JSONObject}
     * @param keyValueEntry  @Map.Entry
     * @param keyString @{@link String}
     * @return JSONObject
     */
    private JSONObject getJsonTypeToParse(Class tableClass, JSONObject reqObj, JSONObject resultObject, Map.Entry keyValueEntry, String keyString) {

        try {

            String valueString = resultObject.optString(keyString);
            String fieldName = (String)keyValueEntry.getValue();
            Field field = tableClass.getDeclaredField(fieldName);


            if (getInstanceOfField(field).equalsIgnoreCase("RealmObject")){
                Object refVALUE = resultObject.opt(keyString);

                reqObj = caseWhenTableExtendsRealmObject(fieldName, field, reqObj, refVALUE);

            }else if (getInstanceOfField(field).equalsIgnoreCase("RealmList")){
                JSONObject refCreate = new JSONObject();
                Object refVALUE = resultObject.opt(keyString);

                reqObj = caseWhenTableExtendsRealmList(fieldName,field, reqObj, refCreate, refVALUE);
            }else {

                reqObj.put(fieldName , valueString);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return reqObj;
    }




    /**
     * @param fieldName
     * @param field
     * @param reqObj
     * @param refVALUE
     * @return
     * @throws JSONException
     */
    private JSONObject caseWhenTableExtendsRealmObject(String fieldName, Field field, JSONObject reqObj, Object refVALUE) throws JSONException {
        JSONObject refCreate = new JSONObject();

        if (refVALUE instanceof JSONArray){
            JSONArray jsonArray = (JSONArray)refVALUE;

            if (jsonArray.length()>0){

                Object checkInstanceVar = jsonArray.get(0);
                if (checkInstanceVar instanceof JSONObject){
                    JSONObject jsonObj = (JSONObject)checkInstanceVar;

                    jsonObj = jsonDECODER(field.getType(), jsonObj);
                    reqObj.put(fieldName , jsonObj);

                }else {
                    refCreate.put("uid", jsonArray.get(0));
                    reqObj.put(fieldName , refCreate);
                }
            }


        }else if (refVALUE instanceof JSONObject){
            JSONObject jsonObject = (JSONObject)refVALUE;
            reqObj.put(fieldName , jsonDECODER(field.getType(), jsonObject));

        }
        return reqObj;
    }




    /**
     *
     * @param fieldName
     * @param reqObj
     * @param refCreate
     * @param refVALUE
     * @return
     * @throws JSONException
     */
    private JSONObject caseWhenTableExtendsRealmList(String fieldName, Field field, JSONObject reqObj, JSONObject refCreate, Object refVALUE) throws JSONException{

        ParameterizedType ge = (ParameterizedType)field.getGenericType();
        Class<?> fieldGetType = (Class<?>) ge.getActualTypeArguments()[0];

        if (refVALUE instanceof JSONArray){

            JSONArray jsonReference = new JSONArray();
            JSONArray jsonArray = (JSONArray)refVALUE;

            for (int i=0; i<jsonArray.length(); i++){
                Object refOBJ = jsonArray.get(i);

                if (refOBJ instanceof JSONObject){
                    JSONObject jsonObj = (JSONObject)refOBJ;

                    jsonObj = jsonDECODER(fieldGetType, jsonObj);
                    jsonReference.put(jsonObj);

                }else {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("uid", refOBJ);
                    jsonReference.put(jsonObj);
                }
            }
            reqObj.put(fieldName , jsonReference);

        }else if (refVALUE instanceof JSONObject){
            JSONArray jsonReference = new JSONArray();

            JSONObject jsonObj = (JSONObject)refVALUE;
            jsonObj = jsonDECODER(fieldGetType, jsonObj);
            jsonReference.put(jsonObj);

            reqObj.put(fieldName , jsonReference);

        }else{

            refCreate.put("uid", refVALUE);
            reqObj.put(fieldName , refCreate);
        }

        return reqObj;
    }




    /**
     * @param field check type of field
     * @return String
     */
    private String getInstanceOfField(Field field) {

        String checkTYPE = field.getType().getSuperclass().getSimpleName();
        String fieldTYPE = null;

        if (checkTYPE.equalsIgnoreCase("RealmObject")){
            fieldTYPE = "RealmObject";
        }else if (checkTYPE.equalsIgnoreCase("AbstractList")){
            fieldTYPE = "RealmList";
        }else {
            fieldTYPE = "Object";
        }
        return fieldTYPE;
    }




    /**
     * It find Class name basis of content_type uid
     * Mapping of class and content type uid
     */
    private void setClassMapping(){

        Set<Class<? extends RealmModel>> MODEL_CLASSES
                = realmPersistenceHelperInstance.getRealmInstance().getConfiguration().getRealmObjectClasses();

        MODEL_CLASSES.forEach(new Consumer<Class<? extends RealmModel>>() {
            @Override
            public void accept(Class<? extends RealmModel> MODEL_CLASS) {
                reflectionThing(MODEL_CLASS);
            }
        });

    }




    /**
     * Takes a class extending to super class (@{@link io.realm.RealmObject}//@{@link RealmModel})
     * and gets its fields and annotated names to map field
     * and their respected keys, it is useful at the time of response parsing.
     * @param MODEL_CLASS
     */
    private void reflectionThing(Class<? extends RealmModel> MODEL_CLASS) {

        for (Annotation annotation : MODEL_CLASS.getAnnotations()) {
            for (Method method : annotation.annotationType().getDeclaredMethods()) {
                try {

                    Object CONTENT_TYPE_NAME =  method.invoke(annotation, (Object[])null);
                    if (!CONTENT_TYPE_NAME.toString().isEmpty() && !CONTENT_TYPE_NAME.toString().equalsIgnoreCase("NO_POLICY")){

                        ArrayMap<String, String> fieldList = new ArrayMap<String, String>();
                        for(Field declaredField  : MODEL_CLASS.getDeclaredFields()) {
                            if (declaredField.isAnnotationPresent(RealmField.class)) {

                                RealmField ann = declaredField.getAnnotation(RealmField.class);
                                fieldList.put(ann.name(), declaredField.getName());
                            }
                        }
                        CLASS_FIELDS_MAPPER.put(MODEL_CLASS, fieldList);
                        CONTENT_TYPE_CLASS_MAPPER.put(CONTENT_TYPE_NAME.toString(), MODEL_CLASS);
                    }else {

                        // For Group If CONTENT_TYPE_NAME will be null then considered Table will be type of group
                        ArrayMap<String, String> fieldList = new ArrayMap<String, String>();
                        for(Field declaredField  : MODEL_CLASS.getDeclaredFields()) {
                            if (declaredField.isAnnotationPresent(RealmField.class)) {

                                RealmField ann = declaredField.getAnnotation(RealmField.class);
                                fieldList.put(ann.name(), declaredField.getName());
                            }
                        }
                        CLASS_FIELDS_MAPPER.put(MODEL_CLASS, fieldList);
                        CONTENT_TYPE_CLASS_MAPPER.put(CONTENT_TYPE_NAME.toString(), MODEL_CLASS);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /**
     *
     * @param sync_token returns updated sync token to find updated data stored to stack
     */
    private void persistsToken(String sync_token, String pagination_token) {

        try {

            realmPersistenceHelperInstance.beginWriteTransaction();
            SyncStore syncStore = new SyncStore();
            syncStore.setUniqueId("token");
            syncStore.setSync_token(sync_token);
            syncStore.setPagination_token(pagination_token);
            //realmPersistenceHelperInstance.getRealmInstance().insertOrUpdate(new SyncStore("token", sync_token, pagination_token));
            realmPersistenceHelperInstance.getRealmInstance().insertOrUpdate(syncStore);
            realmPersistenceHelperInstance.commitWriteTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            realmPersistenceHelperInstance.closeTransaction();
        }
    }



}
