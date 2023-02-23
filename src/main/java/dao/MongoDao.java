package dao;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import core.extractor.MethodInfo;
import core.extractor.MethodInfoProperties;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.LinkedList;
import java.util.List;

public class MongoDao {
    public String DefaultHost     = "localhost";
    public int    DefaultPort     = 27017;
    public String DefaultDbName   = "apis";

    private MongoClient   client   = null;
    private MongoDatabase database = null;

    public MongoDao(String host, int port, String database) {
        if (host != null && !host.trim().isEmpty()) {
            this.DefaultHost = host;
        }

        if (port > 0) {
            this.DefaultPort = port;
        }

        if (database != null && !database.trim().isEmpty()) {
            this.DefaultDbName = database;
        }
    }

    public void init() throws Exception {
        try {
            // 连接到 mongodb 服务
            client = new MongoClient(DefaultHost, DefaultPort);
            // 连接到数据库
            database = client.getDatabase(DefaultDbName);
        } catch (Exception e) {
            throw e;
        }
    }

    public void createCollection(String name) throws Exception {
        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }
        try {
            MongoDatabase db = database;
            db.createCollection(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoCollection<Document> getCollection(String name) throws Exception {
        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }

        try {
            MongoDatabase db = database;
            MongoCollection<Document> collection = db.getCollection(name);
            return collection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertMany(String collection, List<Document> documents) throws Exception {
        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }

        try {
            //插入文档
            //1. 创建文档 org.bson.Document 参数为key-value的格式
            //2. 创建文档集合List<Document>
            //3. 将文档集合插入数据库集合中 mongo
            //Collection.insertMany(List<Document>) 插入单个文档可以用 mongoCollection.insertOne(Document)
            MongoCollection<Document> coll = getCollection(collection);
            if (coll == null) {
                createCollection(collection);
            }
            coll.insertMany(documents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertOne(String collection, Document document) throws Exception {
        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }

        try {
            MongoCollection<Document> coll = getCollection(collection);
            if (coll == null) {
                createCollection(collection);
            }

            coll.insertOne(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检索所有文档
     */
    public List<Document> find(String collection, Bson filter) throws Exception {
        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }

        List<Document> res = new LinkedList<>();
        try {
            MongoCollection<Document> coll = getCollection(collection);
            if (coll == null) {
                return res;
            }

            //检索所有文档
            //1.获取迭代器FindIterable<Document>
            //2.获取游标MongoCursor<Document>
            //3.通过游标遍历检索出的文档集合
            FindIterable<Document> findIterable = coll.find(filter);
            MongoCursor<Document> mongoCursor = findIterable.iterator();


            while (mongoCursor.hasNext()) {
                res.add(mongoCursor.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * 更新文档
     */
    public void updateMany(String collection, Bson filter, Bson updateFields) throws Exception {

        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }

        try {
            // 连接到 mongodb 服务
            MongoCollection<Document> coll = getCollection(collection);
            if (coll == null) {
                return;
            }

            coll.updateMany(filter, updateFields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文档
     */
    public void findOneRemove(String collection, Bson filter) throws Exception{

        if (database == null) {
            throw new IllegalStateException("please init mongodb first");
        }

        try {

            // 连接到 mongodb 服务
            MongoCollection<Document> coll = getCollection(collection);
            if (coll == null) {
                return;
            }

            coll.deleteOne(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将MethodInfo转换为MongoDB存储的Documenet类型对象
     * @param method
     * @return
     */
    public static Document toDocument(MethodInfo method) {
        Document info = new Document();

        info.put(MethodInfoProperties.JavaDoc,      method.javadoc);
        info.put(MethodInfoProperties.MethodName,   method.methodName);
        info.put(MethodInfoProperties.ReturnType,   method.returnType);
        info.put(MethodInfoProperties.Signature,    method.signature);
        info.put(MethodInfoProperties.BodyContent,  method.bodyContent);
        info.put(MethodInfoProperties.ClassName,    method.className);
        info.put(MethodInfoProperties.QualifiedMethodName,  method.qualifiedMethodName);
        info.put(MethodInfoProperties.QualifiedSignature,   method.qualifiedSignature);
        info.put(MethodInfoProperties.QualifiedReturnType,  method.qualifiedReturnType);

        return info;
    }

    /**
     * 将MethodInfo转换为Json格式字符串
     * @param method
     * @return
     */
    public static String toJson(MethodInfo method) {
        return toDocument(method).toJson();
    }
}
