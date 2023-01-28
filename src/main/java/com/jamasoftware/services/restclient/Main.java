package com.jamasoftware.services.restclient;

import com.jamasoftware.services.restclient.exception.RestClientException;
import com.jamasoftware.services.restclient.jamadomain.core.JamaInstance;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.*;

import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void printAll(JamaParent jamaParent, int indent) throws RestClientException {
        String indentString = "";
        for(int i = 0; i < indent; ++i) {
            indentString += "==";
        }
        if(jamaParent.isProject()) {
            JamaProject project = (JamaProject)jamaParent;
            logger.info(indentString + project.getName() + " - " + project.getId());
        } else {
            JamaItem item = (JamaItem)jamaParent;
            logger.info(indentString + item.getName() + " - " + item.getSequence());
        }
        for(JamaItem child : jamaParent.getChildren()) {
            printAll(child, indent + 1);
        }
    }

    // @SuppressWarnings("unchecked")
    public static void main(String[] ignore) throws UnsupportedEncodingException, RestClientException {



        try {
            int itemid = 0;
            String propfilepath = null;
            for(String arg : ignore) {
                if (arg.startsWith("-item:")) {
                    String[] param = arg.split(":");
                    if( param.length > 1) {
                        itemid = Integer.parseInt(param[1]);
                    } 
                }
                if (arg.startsWith("-property:")) {
                    String[] param = arg.split(":");
                    if( param.length > 1) {
                        propfilepath = param[1];
                    } 
                }
            }


            if(itemid == 0) {
                System.out.println("option -item:itemid is must");
                return;
            }

            if(propfilepath == null) {
                System.out.println("option -property:filepath is must");
                return;
            }

            // TODO fail good (John) attempted to retireve item types for invalid item
            JamaInstance jamaInstance = new JamaInstance(new JamaConfig(true, propfilepath));


            // API Header for JamaConfig object
//            JamaConfig jamaConfig = new JamaConfig(false);
//            jamaConfig.setApiKey("SUPER_SECRET_KEY");
//            jamaConfig.setBaseUrl("https://{baseURL}.com");
//            jamaConfig.setUsername("api_user");
//            jamaConfig.setPassword("password");
//            jamaConfig.setResourceTimeOut(6);
//            JamaInstance jamaInstance = new JamaInstance(jamaConfig);


            JamaItem fromItem = jamaInstance.getItem(itemid);
            printAll(fromItem,0);

//            JamaItem toDelete = jamaInstance.getItem(2281076);
//            toDelete.edit().delete();  // OR
//            jamaInstance.deleteItem(2281075);
//
//            JamaRelationship newRelationship = fromItem.getDownstreamRelationships().get(0);
//            newRelationship.edit().delete(); // OR
//            jamaInstance.deleteRelationship(newRelationship.getId());



            // JamaRelationship relationship = jamaInstance.getRelationship(294902);
            // JamaItem fromItem = relationship.getFromItem();
            // JamaItem toItem = relationship.getToItem();
            // JamaRelationshipType relationshipType = relationship.getRelationshipType();
            // System.out.println("relationsghip:" + relationship.toString() );



            // Update Relationship
//            JamaRelationshipType relationshipType = jamaInstance.getRelationshipTypes().get(0);
//            JamaRelationship newRelationship = fromItem.getDownstreamRelationships().get(0);
//            JamaRelationship updatedRelationship = newRelationship.edit().setFromItem(fromItem).setToItem(toItem).setRelationshipType(relationshipType).commit();
//            logger.info(updatedRelationship);






            // Create New Relationship
//            JamaRelationship create = new JamaRelationship();
//            create.associate(jamaInstance);
//            JamaRelationship newlycreated = create.edit().setFromItem(fromItem).setToItem(toItem).setRelationshipType(relationshipType).commit();
//            System.out.println(newlycreated.toString());
            // System.out.println("done");
//














            //            itemA.edit().setFieldValue("description", unicodeString).commit();
//            JamaFieldValue fieldValue = itemA.getFieldValueByName("description");
//            logger.info(fieldValue.getValue());
//            assertTrue(unicodeString.equals(fieldValue.getValue().toString()));

//            JamaItemType itemType = jamaInstance.getItemType(89009);
//            ArrayList<JamaField> fields = (ArrayList<JamaField>) itemType.getFields();
//            for(JamaField field : fields) {
//                if(field.type.equals("DATE")) {
//                    logger.info(field.getValue().getName());
//                }
//            }
//            String description = jamaItem.getFieldValueByName("description").getValue().toString();
//            updated = updated.edit().setFieldValue("description", description).commit();
//            logger.info(updated.getFieldValueByName("description"));
//            logger.info("done");

//            logger.info("Jama item is " + jamaItem.isLocked() + " lock status");
//            logger.info("Jama item is locked by : " + jamaItem.lockedBy().getUsername());
//            logger.info("Jama item is " + jamaItem.isLockedByCurrentUser() + " locked by current user");
//            logger.info("Now unlocking item");
//
////            for org admin users with override capabilities:
////            jamaItem.unlock();
////            jamaItem.lock();     //optional to acquire lock on the item
//
////            for non org admin users, they will need to verify unlocking/acquiring lock before proceeding:
//            logger.info(jamaItem.releaseLock());     //will return false if item could not be unlocked, true otherwise
//            logger.info(jamaItem.acquireLock());     //will return true if item was locked, false otherwise
//
//            logger.info("Status to release lock : " + jamaItem.releaseLock());
//            logger.info("I have gotten the lock with status : " + jamaItem.acquireLock());
//            logger.info("Jama item is " + jamaItem.isLocked() + " lock status");
//            logger.info("Jama item is " + jamaItem.isLockedByCurrentUser() + " locked by current user");


//            JamaItem newParentFolder = jamaInstance.getItem(1972340);
//            JamaParent jamaParent = jamaItem.getParent();
//            logger.info("Jama item: " + jamaItem.toString() + " has parent: " + jamaParent.toString());
//
//            logger.info("And this item's open url is : " + jamaInstance.getOpenUrl(jamaItem));
//            JamaProject jamaProject = jamaInstance.getProject(2120041);
//
////            logger.info("New jama parent is: " + newParentFolder.toString());


//            logger.info(jamaItem.getName());
//            logger.info("Added child to new parent");
//            logger.info("NOW:: Jama item: " + jamaItem.toString() + " has parent: " + jamaItem.getParent().toString());

//            jamaItem.associate(1972342, jamaInstance);
//            logger.info(jamaItem);
//            JamaProject jamaProject = new JamaProject();
//            jamaProject.associate(20183, jamaInstance);
//            JamaItemType jamaItemType = new JamaItemType();
//            jamaItemType.associate(89029, jamaInstance);
//          //  logger.info(jamaItemType.getDisplay());
//            jamaItemType.getImage();

//            JamaProject jamaProject = jamaInstance.getProject(20183);
//            jamaInstance.getItemTypes();

//            JamaItem jamaItem = jamaInstance.getItem(2119354);
//            jamaItem.getFieldValues();
//            JamaFieldValue fieldValue = jamaItem.getFieldValueByName("description");
//            logger.info(fieldValue.toString());
//            jamaInstance.createItem("name", JamaParent, JamaIteType)
//            List<JamaRelationshipType> relationshipTypes = jamaInstance.getRelationshipTypes();

//            JamaProject jamaProject = jamaInstance.getProject(20183);
//            List<JamaItem> children = jamaProject.getChildren();
//            for(JamaItem j : children) {
//                logger.info(j.toString());
//            }

//            jamaInstance.createRelationship(relationshipId, fromItem, toItem);
//            jamaItem.edit().setFieldValue("Name", "Timbuktoo").setFieldValue("description", "Not your description;=").commit();
//            jamaItem.edit()
//                    .setFieldValue("status", "approved")
//                    .commit();
//            for(JamaFieldValue value : jamaItem.getFieldValues()) {
//                logger.info(value);
//            }

//
//            JamaItem component = jamaInstance
//                    .createItem("John Component", jamaProject, jamaInstance.getItemType("Component"))
//                    .commit();
//
//            jamaInstance.createItem();
//
//            JamaItem set = jamaInstance
//                    .createItem("John Set", component, jamaInstance.getItemType("Set"))
//                    .setChildItemType(jamaInstance.getItemType("Requirement"))
//                    .setFieldValue("setKey", jamaInstance.getItemType("Requirement").getTypeKey())
//                    .commit();
//            set.getItemTypeImage();
//            set.getChildItemType().getImage();
//
//            JamaItem item = jamaInstance
//                    .createItem("John Item", set, jamaInstance.getItemType("Requirement"))
//                    .setFieldValue("description", "hi nathan")
//                    .commit();
//
//            item.getItemType().getImage();



//            StagingItem stagingItem = item.edit();
//            stagingItem.setFieldValue("status", "approved");
//            stagingItem.commit();
//            stagingItem.setFieldValue("BREAK", "BUSTED");


//
//            logger.info(component.getDocumentKey());
//            logger.info(set.getDocumentKey());
//            logger.info(item.getDocumentKey());
//
//            jamaItem.edit()
//                    .setName("NEW NAME ALERT")
//                    .setFieldValue("description", "textblahblhablhasdbkljhsdflkhjasfkljhsd")
//                    .commit();

//            jamaItem.edit()
//                    .setName("Edited")
//                    .setDescription("Desc")
//                    .commit();
//            jamaInstance.createItem("name", jamaProject, jamaInstance.getItemType("Text"))
//                    .setName("Day before Thanksgiving")
//                    .commit();

//            jamaItem.associate(2119331, jamaInstance);

//            logger.info("Item is: " + jamaItem);
//            logger.info("Parent is: " + jamaItem.getParent());
//            logger.info("blah");
//            List<JamaItem> children = jamaItem.getChildren();
//            logger.info("CHildren are : ");
//            for(JamaItem item: children){
//                logger.info(item);
//            }
//            printAll(jamaItem, 0);
//            printAll(jamaItem, 0);
//            JamaItem item = new JamaItem();
//            item.associate(2119533, jamaInstance);
//            byte[] imageData = item.getItemTypeImage();
//            FileOutputStream fos = new FileOutputStream("out.png");
//            fos.write(imageData);
//            fos.close();

//            logger.info(item.isLocked());
//            logger.info(item.lockedBy());
//            item.lock();
//            logger.info(item.isLocked());
////            logger.info(item.lockedBy());
//            List<JamaProject> hostedProjects = jamaInstance.getProjects();
//            JamaProject aProject = hostedProjects.get(0);
//            List<JamaItem> items = aProject.getItems();
//            for(JamaItem item : items){
//                logger.info(item);
//            }
//            for(JamaItem item : items) {
//                if(item.getName().toString().contains("dont care")) {
//                    item.forceLockItem();
//                    if(item.isLockedByCurrentUser())
//                        logger.info("DONZO");
////                    JamaItem upItem = item.getUpstreamItems().get(0);
//                    logger.info(upItem);
//                    logger.info("-----");
//                    logger.info(upItem.getDownstreamItems().get(0));
//                    logger.info("Hellow");




//            List<JamaRelationship> relationships = aProject.getRelationships();
//            JamaRelationship relationship = relationships.get(0);
//            logger.info(relationship.getFromItem());
//            logger.info(relationship.getToItem());
//            logger.info(relationship.getRelationshipType());

//            JamaProject jamaProject = new JamaProject();
//            jamaProject.associate(20540, jamaInstance);
//            List<JamaItemType> types = jamaInstance.getItemTypes();
//            JamaItem jamaItem = jamaInstance.getItem(2169992);
//            logger.info(jamaItem.getName());
//            jamaInstance.ping();
//            JamaProject jamaProject = null;
//            List<JamaProject> hostedProjects = jamaInstance.getProjects();
//            List<JamaItem> items = new ArrayList<>();
//            for(JamaProject project : hostedProjects) {
//                if(project.getName().equals("zzzzz")) {
//                    jamaProject = project;
//                    items.addAll(project.getItems());
//                }
//            }
//            List<JamaItem> items = jamaProject.getItems();
//
//            for (JamaItem item : items) {
//                logger.info(item.getName());
//            }
//
//            List<JamaRelationship> relationships = items.get(1).getDownstreamRelationships();
            //JamaItem item = jamaInstance.getItem(1972370);
//            item.getName();

            logger.info("done");

        } catch(Exception e) {
            logger.error(e);;
        }
    }
}
