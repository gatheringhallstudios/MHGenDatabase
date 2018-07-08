package com.ghstudios.android.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Xml

import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.data.cursors.ASBSessionCursor
import com.ghstudios.android.data.cursors.ASBSetCursor
import com.ghstudios.android.data.cursors.ArenaQuestCursor
import com.ghstudios.android.data.cursors.ArenaRewardCursor
import com.ghstudios.android.data.cursors.ArmorCursor
import com.ghstudios.android.data.cursors.CombiningCursor
import com.ghstudios.android.data.cursors.ComponentCursor
import com.ghstudios.android.data.cursors.DecorationCursor
import com.ghstudios.android.data.cursors.GatheringCursor
import com.ghstudios.android.data.cursors.HornMelodiesCursor
import com.ghstudios.android.data.cursors.HuntingRewardCursor
import com.ghstudios.android.data.cursors.ItemCursor
import com.ghstudios.android.data.cursors.ItemToMaterialCursor
import com.ghstudios.android.data.cursors.ItemToSkillTreeCursor
import com.ghstudios.android.data.cursors.LocationCursor
import com.ghstudios.android.data.cursors.MonsterAilmentCursor
import com.ghstudios.android.data.cursors.MonsterDamageCursor
import com.ghstudios.android.data.cursors.MonsterHabitatCursor
import com.ghstudios.android.data.cursors.MonsterStatusCursor
import com.ghstudios.android.data.cursors.MonsterToArenaCursor
import com.ghstudios.android.data.cursors.MonsterToQuestCursor
import com.ghstudios.android.data.cursors.MonsterWeaknessCursor
import com.ghstudios.android.data.cursors.PalicoWeaponCursor
import com.ghstudios.android.data.cursors.QuestCursor
import com.ghstudios.android.data.cursors.QuestRewardCursor
import com.ghstudios.android.data.cursors.SkillCursor
import com.ghstudios.android.data.cursors.SkillTreeCursor
import com.ghstudios.android.data.cursors.WeaponCursor
import com.ghstudios.android.data.cursors.WeaponTreeCursor
import com.ghstudios.android.data.cursors.WishlistComponentCursor
import com.ghstudios.android.data.cursors.WishlistCursor
import com.ghstudios.android.data.cursors.WishlistDataCursor
import com.ghstudios.android.data.cursors.WyporiumTradeCursor
import com.ghstudios.android.data.util.QueryHelper
import com.ghstudios.android.mhgendatabase.R
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Arrays
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/*
   QUERY REFERENCE:

For queries with no JOINs:
	- call wrapHelper()
	- set values for
			_Distinct
			_Table
			_Columns
			_Selection
			_SelectionArgs
			_GroupBy
			_Having
			_OrderBy
			_Limit

For queries with JOINs:
	- call wrapJoinHelper(SQLiteQueryBuilder qb)
	= set values for
		_Columns
		_Selection
		_SelectionArgs
		_GroupBy
		_Having
		_OrderBy
		_Limit

*/


//Version 1 - v1.0   - Initial Release
//Version 2 - v1.0.1 - Added Alternate Damages/Weaknesses/Ailments
//Version 3 - v1.0.2 - Fixed issues with some quest data
//Version 4 - v1.1.0 - Changed Weaknesses / Fixed some data bugs / Default Wishlists+Set
//Version 5 - v1.1.1 - Added more localized data / July DLC
//Version 6 - v1.1.2 - Sept/Oct DLC

private val DATABASE_NAME = "mhgen.db"
private val DATABASE_VERSION = 6

/**
 * Initialize the helper object
 *
 * @param context
 */
internal class MonsterHunterDatabaseHelper constructor(ctx: Context):
        SQLiteAssetHelper(ctx.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TAG = "MonsterHunterDatabaseHelper"

    // Use the application context, which will ensure that you
    // don't accidentally leak an Activity's context.
    // See this article for more information: http://bit.ly/6LRzfx
    private val myContext = ctx.applicationContext

    init {
        setForcedUpgrade()
    }

    companion object {

        // This class uses an application context, which has no leak. So ignore the warning.
        // also note: lateinit fails with JvmStatic
        @SuppressLint("StaticFieldLeak")
        private var mInstance: MonsterHunterDatabaseHelper? = null

        private val lock = ReentrantReadWriteLock()

        /**
         * Returns Singleton instance of the helper object
         *
         * @param c Application context
         * @return Singleton instance of helper
         */
        @JvmStatic fun getInstance(ctx: Context): MonsterHunterDatabaseHelper {
            lock.read {
                if (mInstance != null) {
                    return mInstance!!
                }

                //JOE:This will force database to be copied.
                //c.getApplicationContext().deleteDatabase(DATABASE_NAME);
                lock.write {
                    mInstance = MonsterHunterDatabaseHelper(ctx)
                    return mInstance!!
                }
            }
        }
    }

    fun isTableExists(tableName: String, db: SQLiteDatabase): Boolean {
        val cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'",
                null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.close()
                return true
            }
            cursor.close()
        }
        return false
    }

    override fun preCopyDatabase(db: SQLiteDatabase) {
        //Log.w(TAG, "Pre forcing database upgrade!");
        val filename = "wishlist.xml"
        val fos: FileOutputStream

        try {
            val asb_set_columns = arrayOf(S.COLUMN_ASB_SET_NAME, S.COLUMN_ASB_SET_RANK, S.COLUMN_ASB_SET_HUNTER_TYPE, S.COLUMN_HEAD_ARMOR_ID, S.COLUMN_HEAD_DECORATION_1_ID, S.COLUMN_HEAD_DECORATION_2_ID, S.COLUMN_HEAD_DECORATION_3_ID, S.COLUMN_BODY_ARMOR_ID, S.COLUMN_BODY_DECORATION_1_ID, S.COLUMN_BODY_DECORATION_2_ID, S.COLUMN_BODY_DECORATION_3_ID, S.COLUMN_ARMS_ARMOR_ID, S.COLUMN_ARMS_DECORATION_1_ID, S.COLUMN_ARMS_DECORATION_2_ID, S.COLUMN_ARMS_DECORATION_3_ID, S.COLUMN_WAIST_ARMOR_ID, S.COLUMN_WAIST_DECORATION_1_ID, S.COLUMN_WAIST_DECORATION_2_ID, S.COLUMN_WAIST_DECORATION_3_ID, S.COLUMN_LEGS_ARMOR_ID, S.COLUMN_LEGS_DECORATION_1_ID, S.COLUMN_LEGS_DECORATION_2_ID, S.COLUMN_LEGS_DECORATION_3_ID, S.COLUMN_TALISMAN_EXISTS, S.COLUMN_TALISMAN_TYPE, S.COLUMN_TALISMAN_SLOTS, S.COLUMN_TALISMAN_DECORATION_1_ID, S.COLUMN_TALISMAN_DECORATION_2_ID, S.COLUMN_TALISMAN_DECORATION_3_ID, S.COLUMN_TALISMAN_SKILL_1_ID, S.COLUMN_TALISMAN_SKILL_1_POINTS, S.COLUMN_TALISMAN_SKILL_2_ID, S.COLUMN_TALISMAN_SKILL_2_POINTS)
            val asb_set_columns_list = Arrays.asList(*asb_set_columns)

            val wishlist_columns = arrayOf(S.COLUMN_WISHLIST_ID, S.COLUMN_WISHLIST_NAME)
            val wishlist_columns_list = Arrays.asList(*wishlist_columns)

            val wishlist_data_columns = arrayOf(S.COLUMN_WISHLIST_DATA_ID, S.COLUMN_WISHLIST_DATA_WISHLIST_ID, S.COLUMN_WISHLIST_DATA_ITEM_ID, S.COLUMN_WISHLIST_DATA_QUANTITY, S.COLUMN_WISHLIST_DATA_SATISFIED, S.COLUMN_WISHLIST_DATA_PATH)
            val wishlist_data_columns_list = Arrays.asList(*wishlist_data_columns)

            val wishlist_component_columns = arrayOf(S.COLUMN_WISHLIST_COMPONENT_ID, S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID, S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID, S.COLUMN_WISHLIST_COMPONENT_QUANTITY, S.COLUMN_WISHLIST_COMPONENT_NOTES)
            val wishlist_component_columns_list = Arrays.asList(*wishlist_component_columns)

            fos = myContext.openFileOutput(filename, Context.MODE_PRIVATE)
            val serializer = Xml.newSerializer()
            serializer.setOutput(fos, "UTF-8")
            serializer.startDocument(null, java.lang.Boolean.valueOf(true))
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

            if (isTableExists(S.TABLE_WISHLIST, db)) {
                val wc = db.rawQuery("SELECT * FROM " + S.TABLE_WISHLIST, null)
                wc.moveToFirst()

                serializer.startTag(null, "wishlists")
                while (!wc.isAfterLast) {
                    serializer.startTag(null, "wishlist")

                    for (wishlist_column in wishlist_columns_list) {
                        serializer.startTag(null, wishlist_column)
                        if (wc.isNull(wc.getColumnIndex(wishlist_column))) {
                            serializer.text("")
                        } else {
                            if (wishlist_column == S.COLUMN_WISHLIST_NAME) {
                                serializer.text(wc.getString(wc.getColumnIndex(wishlist_column)))
                            } else {
                                serializer.text(Integer.toString(wc.getInt(wc.getColumnIndex(wishlist_column))))
                            }
                        }
                        serializer.endTag(null, wishlist_column)
                    }

                    serializer.endTag(null, "wishlist")

                    wc.moveToNext()
                }
                serializer.endTag(null, "wishlists")
                wc.close()
            }

            if (isTableExists(S.TABLE_WISHLIST_DATA, db)) {
                val wdc = db.rawQuery("SELECT * FROM " + S.TABLE_WISHLIST_DATA, null)
                wdc.moveToFirst()

                serializer.startTag(null, "wishlist_data")
                while (!wdc.isAfterLast) {
                    serializer.startTag(null, "data")

                    for (data_column in wishlist_data_columns_list) {
                        serializer.startTag(null, data_column)
                        if (wdc.isNull(wdc.getColumnIndex(data_column))) {
                            serializer.text("")
                        } else {
                            if (data_column == S.COLUMN_WISHLIST_DATA_PATH) {
                                serializer.text(wdc.getString(wdc.getColumnIndex(data_column)))
                            } else {
                                serializer.text(Integer.toString(wdc.getInt(wdc.getColumnIndex(data_column))))
                            }
                        }
                        serializer.endTag(null, data_column)
                    }

                    serializer.endTag(null, "data")

                    wdc.moveToNext()
                }
                serializer.endTag(null, "wishlist_data")
                wdc.close()
            }

            if (isTableExists(S.TABLE_WISHLIST_COMPONENT, db)) {
                val wcc = db.rawQuery("SELECT * FROM " + S.TABLE_WISHLIST_COMPONENT, null)
                wcc.moveToFirst()

                serializer.startTag(null, "wishlist_components")
                while (!wcc.isAfterLast) {
                    serializer.startTag(null, "component")

                    for (component_column in wishlist_component_columns_list) {
                        serializer.startTag(null, component_column)
                        if (wcc.isNull(wcc.getColumnIndex(component_column))) {
                            serializer.text("")
                        } else {
                            serializer.text(Integer.toString(wcc.getInt(wcc.getColumnIndex(component_column))))
                        }

                        serializer.endTag(null, component_column)
                    }

                    serializer.endTag(null, "component")

                    wcc.moveToNext()
                }
                serializer.endTag(null, "wishlist_components")
                wcc.close()
            }

            if (isTableExists(S.TABLE_ASB_SETS, db)) {
                val asbc = db.rawQuery("SELECT * FROM " + S.TABLE_ASB_SETS, null)
                asbc.moveToFirst()

                serializer.startTag(null, "asb_sets")
                while (!asbc.isAfterLast) {
                    serializer.startTag(null, "asb_set")

                    for (asb_column in asb_set_columns_list) {
                        serializer.startTag(null, asb_column)
                        if (asbc.isNull(asbc.getColumnIndex(asb_column))) {
                            serializer.text("")
                        } else {
                            if (asb_column == S.COLUMN_ASB_SET_NAME) {
                                serializer.text(asbc.getString(asbc.getColumnIndex(asb_column)))
                            } else {
                                serializer.text(Integer.toString(asbc.getInt(asbc.getColumnIndex(asb_column))))
                            }
                        }

                        serializer.endTag(null, asb_column)
                    }
                    serializer.endTag(null, "asb_set")

                    asbc.moveToNext()
                }
                serializer.endTag(null, "asb_sets")
                asbc.close()
            }

            serializer.endDocument()
            serializer.flush()
            fos.close()
        } catch (e: IOException) {
            //e.printStackTrace();
        }

    }

    protected enum class Tags {
        WISHLIST, WISHLIST_DATA, WISHLIST_COMPONENTS, ASB_SET, OTHER
    }

    override fun postCopyDatabase(db: SQLiteDatabase) {
        //Log.w(TAG, "Post forcing database upgrade!");
        val filename = "wishlist.xml"

        var fis: FileInputStream? = null
        val isr: InputStreamReader? = null
        val data: String

        try {
            fis = myContext.openFileInput(filename)

            val wishlist_id: Long = 0
            val name = ""
            val item_id: Long = 0
            val quantity = 0
            val satisfied = 0
            val path = ""
            val notes = 0
            var text = ""
            var clear_wishlist = true

            val xmlFactoryObject = XmlPullParserFactory.newInstance()
            val myParser = xmlFactoryObject.newPullParser()
            myParser.setInput(fis, null)

            var current_tag = Tags.OTHER

            val asb_set_tables = arrayOf(S.COLUMN_ASB_SET_NAME, S.COLUMN_ASB_SET_RANK, S.COLUMN_ASB_SET_HUNTER_TYPE, S.COLUMN_HEAD_ARMOR_ID, S.COLUMN_HEAD_DECORATION_1_ID, S.COLUMN_HEAD_DECORATION_2_ID, S.COLUMN_HEAD_DECORATION_3_ID, S.COLUMN_BODY_ARMOR_ID, S.COLUMN_BODY_DECORATION_1_ID, S.COLUMN_BODY_DECORATION_2_ID, S.COLUMN_BODY_DECORATION_3_ID, S.COLUMN_ARMS_ARMOR_ID, S.COLUMN_ARMS_DECORATION_1_ID, S.COLUMN_ARMS_DECORATION_2_ID, S.COLUMN_ARMS_DECORATION_3_ID, S.COLUMN_WAIST_ARMOR_ID, S.COLUMN_WAIST_DECORATION_1_ID, S.COLUMN_WAIST_DECORATION_2_ID, S.COLUMN_WAIST_DECORATION_3_ID, S.COLUMN_LEGS_ARMOR_ID, S.COLUMN_LEGS_DECORATION_1_ID, S.COLUMN_LEGS_DECORATION_2_ID, S.COLUMN_LEGS_DECORATION_3_ID, S.COLUMN_TALISMAN_EXISTS, S.COLUMN_TALISMAN_TYPE, S.COLUMN_TALISMAN_SLOTS, S.COLUMN_TALISMAN_DECORATION_1_ID, S.COLUMN_TALISMAN_DECORATION_2_ID, S.COLUMN_TALISMAN_DECORATION_3_ID, S.COLUMN_TALISMAN_SKILL_1_ID, S.COLUMN_TALISMAN_SKILL_1_POINTS, S.COLUMN_TALISMAN_SKILL_2_ID, S.COLUMN_TALISMAN_SKILL_2_POINTS)
            val asb_set_tables_list = Arrays.asList(*asb_set_tables)

            val wishlist_columns = arrayOf(S.COLUMN_WISHLIST_ID, S.COLUMN_WISHLIST_NAME)
            val wishlist_columns_list = Arrays.asList(*wishlist_columns)

            val wishlist_data_columns = arrayOf(S.COLUMN_WISHLIST_DATA_ID, S.COLUMN_WISHLIST_DATA_WISHLIST_ID, S.COLUMN_WISHLIST_DATA_ITEM_ID, S.COLUMN_WISHLIST_DATA_QUANTITY, S.COLUMN_WISHLIST_DATA_SATISFIED, S.COLUMN_WISHLIST_DATA_PATH)
            val wishlist_data_columns_list = Arrays.asList(*wishlist_data_columns)

            val wishlist_component_columns = arrayOf(S.COLUMN_WISHLIST_COMPONENT_ID, S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID, S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID, S.COLUMN_WISHLIST_COMPONENT_QUANTITY, S.COLUMN_WISHLIST_COMPONENT_NOTES)
            val wishlist_component_columns_list = Arrays.asList(*wishlist_component_columns)

            //HashMap<String, String> row_hash = new HashMap<String, String>();
            val row_values = ContentValues()

            var event = myParser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                val tagName = myParser.name
                when (event) {
                    XmlPullParser.START_TAG -> if (tagName == "asb_set") {
                        row_values.clear()
                        //row_hash.clear();
                        current_tag = Tags.ASB_SET
                    } else if (tagName == "wishlist") {
                        row_values.clear()
                        //row_hash.clear();
                        current_tag = Tags.WISHLIST
                    } else if (tagName == "data") {
                        row_values.clear()
                        //row_hash.clear();
                        current_tag = Tags.WISHLIST_DATA
                    } else if (tagName == "component") {
                        row_values.clear()
                        //row_hash.clear();
                        current_tag = Tags.WISHLIST_COMPONENTS
                    } else if (tagName == "asb_sets") {
                        db.delete(S.TABLE_ASB_SETS, null, null)
                    }
                    XmlPullParser.TEXT -> text = myParser.text

                    XmlPullParser.END_TAG -> {
                        if (tagName == "asb_set") {
                            current_tag = Tags.OTHER
                            db.insert(S.TABLE_ASB_SETS, null, row_values)
                        } else if (tagName == "wishlist") {
                            current_tag = Tags.OTHER
                            if (clear_wishlist) {
                                db.delete(S.TABLE_WISHLIST, null, null)
                                //only clear the table once if there is data to load
                                clear_wishlist = false
                            }
                            db.insert(S.TABLE_WISHLIST, null, row_values)
                        } else if (tagName == "data") {
                            current_tag = Tags.OTHER
                            db.insert(S.TABLE_WISHLIST_DATA, null, row_values)
                        } else if (tagName == "component") {
                            current_tag = Tags.OTHER
                            db.insert(S.TABLE_WISHLIST_COMPONENT, null, row_values)
                        }

                        if (current_tag == Tags.ASB_SET) {
                            if (asb_set_tables_list.contains(tagName)) {
                                if (tagName == S.COLUMN_ASB_SET_NAME) {
                                    row_values.put(tagName, text)
                                } else if (text.trim { it <= ' ' } == "") {
                                    row_values.putNull(tagName)
                                } else {
                                    try {
                                        row_values.put(tagName, Integer.valueOf(text))
                                    } catch (e: NumberFormatException) {
                                        row_values.putNull(tagName)
                                    }

                                }
                            }
                        } else if (current_tag == Tags.WISHLIST) {
                            if (wishlist_columns_list.contains(tagName)) {
                                if (tagName == S.COLUMN_WISHLIST_NAME) {
                                    row_values.put(tagName, text)
                                } else if (text.trim { it <= ' ' } == "") {
                                    row_values.putNull(tagName)
                                } else {
                                    try {
                                        row_values.put(tagName, Integer.valueOf(text))
                                    } catch (e: NumberFormatException) {
                                        row_values.putNull(tagName)
                                    }

                                }
                            }
                        } else if (current_tag == Tags.WISHLIST_DATA) {
                            if (wishlist_data_columns_list.contains(tagName)) {
                                if (tagName == S.COLUMN_WISHLIST_DATA_PATH) {
                                    row_values.put(tagName, text)
                                } else if (text.trim { it <= ' ' } == "") {
                                    row_values.putNull(tagName)
                                } else {
                                    try {
                                        row_values.put(tagName, Integer.valueOf(text))
                                    } catch (e: NumberFormatException) {
                                        row_values.putNull(tagName)
                                    }

                                }
                            }
                        } else if (current_tag == Tags.WISHLIST_COMPONENTS) {
                            if (wishlist_component_columns_list.contains(tagName)) {
                                if (text.trim { it <= ' ' } == "") {
                                    row_values.putNull(tagName)
                                } else {
                                    try {
                                        row_values.put(tagName, Integer.valueOf(text))
                                    } catch (e: NumberFormatException) {
                                        row_values.putNull(tagName)
                                    }

                                }
                            }
                        }
                    }
                }
                event = myParser.next()
            }
            fis!!.close()
        } catch (e: Exception) {
            //e.printStackTrace();
        }

    }

    override fun onForcedUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        super.onForcedUpgrade(db, oldVersion, newVersion)

        when (oldVersion) {
        //Fall through, so all old versions will perform the updates
            1, 2, 3, 4 -> {
                //Database version 4 removed G rank from ASB
                db.execSQL("UPDATE " + S.TABLE_ASB_SETS + " SET " + S.COLUMN_ASB_SET_RANK + "=1 WHERE " + S.COLUMN_ASB_SET_RANK + ">=2")

                //Create a default wishlist if none exist
                var c: Cursor? = db.rawQuery("SELECT COUNT(*) FROM " + S.TABLE_WISHLIST, null)
                if (c!!.moveToFirst() && c.getInt(0) == 0) {
                    val cv = ContentValues()
                    cv.put(S.COLUMN_WISHLIST_NAME, myContext.getString(R.string.default_wishlist_name))
                    db.insert(S.TABLE_WISHLIST, null, cv)
                }
                c?.close()

                //Create a default armor set
                c = db.rawQuery("SELECT COUNT(*) FROM " + S.TABLE_ASB_SETS, null)
                if (c!!.moveToFirst() && c.getInt(0) == 0) {
                    val cv = ContentValues()
                    cv.put(S.COLUMN_ASB_SET_NAME, myContext.getString(R.string.default_armor_set_name))
                    cv.put(S.COLUMN_ASB_SET_RANK, 0)
                    cv.put(S.COLUMN_ASB_SET_HUNTER_TYPE, 0)
                    cv.put(S.COLUMN_TALISMAN_EXISTS, 0)
                    db.insert(S.TABLE_ASB_SETS, null, cv)
                }
                c?.close()


            }
        }

    }

    /**
     * Close database
     */
    @Synchronized
    override fun close() {
        super.close()
    }

    private fun makePlaceholders(len: Int): String {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw RuntimeException("No placeholders")
        } else {
            val sb = StringBuilder(len * 2 - 1)
            sb.append("?")
            for (i in 1 until len) {
                sb.append(",?")
            }
            return sb.toString()
        }
    }

    /*
     * Helper method: used for queries that has no JOINs
     */
    private fun wrapHelper(qh: QueryHelper): Cursor {
        return writableDatabase.query(qh.Distinct, qh.Table, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)
    }

    /*
     * Helper method: used for queries that has no JOINs
     */
    private fun wrapHelper(db: SQLiteDatabase, qh: QueryHelper): Cursor {
        return db.query(qh.Distinct, qh.Table, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)
    }

    /*
     * Helper method: used for queries that has JOINs
     */
    private fun wrapJoinHelper(qb: SQLiteQueryBuilder, qh: QueryHelper): Cursor {
        //		Log.d(TAG, "qb: " + qb.buildQuery(_Columns, _Selection, _SelectionArgs, _GroupBy, _Having, _OrderBy, _Limit));
        return qb.query(writableDatabase, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)
    }

    /*
     * Helper method: used for queries that has JOINs
     */
    private fun wrapJoinHelper(db: SQLiteDatabase, qb: SQLiteQueryBuilder, qh: QueryHelper): Cursor {
        //		Log.d(TAG, "qb: " + qb.buildQuery(_Columns, _Selection, _SelectionArgs, _GroupBy, _Having, _OrderBy, _Limit));
        return qb.query(db, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)
    }

    /**
     * Modifies a query helper to perform searching.
     * The current algorithm is "beginning of word, union match all words"
     * Modifies Selection and and SelectionArgs
     * Note: This fails if there is already arguments on the query.
     * TODO: support arguments already existing by expanding the array and adding "AND"
     * @param qh
     * @param columnName
     * @param searchTerm
     */
    private fun modifyQueryForSearch(qh: QueryHelper, columnName: String, searchTerm: String) {
        // WHERE (name LIKE '% word1%' OR name LIKE 'word1%')
        //   AND (name LIKE '% word2%' OR name LIKE 'word2%')

        val words = searchTerm.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val selectionArgs = arrayOfNulls<String>(words.size * 2)
        qh.Selection = ""
        for (i in words.indices) {
            if (i != 0) {
                qh.Selection += " AND "
            }
            qh.Selection += "($columnName LIKE ? OR $columnName LIKE ?)"
            selectionArgs[i * 2] = "% ${words[i]}%"
            selectionArgs[i * 2 + 1] = words[i] + "%"
        }

        qh.SelectionArgs = selectionArgs
    }

    /*
     * Insert data to a table
     */
    fun insertRecord(table: String, values: ContentValues): Long {
        return writableDatabase.insert(table, null, values)
    }

    /*
     * Insert data to a table
     */
    fun insertRecord(db: SQLiteDatabase, table: String, values: ContentValues): Long {
        return db.insert(table, null, values)
    }

    /*
     * Update data in a table
     */
    fun updateRecord(table: String, strFilter: String, values: ContentValues): Int {
        return writableDatabase.update(table, values, strFilter, null)
    }

    /*
     * Delete data in a table
     */
    fun deleteRecord(table: String, where: String, args: Array<String>): Boolean {
        return writableDatabase.delete(table, where, args) > 0
    }

    /**
     * ****************************** ARENA QUEST QUERIES *****************************************
     */

    /*
	 * Get all arena quests
	 */
    fun queryArenaQuests(): ArenaQuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARENA_QUESTS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArenaQuestCursor(wrapJoinHelper(builderArenaQuest(), qh))
    }

    /*
     * Get a specific arena quest
     */
    fun queryArenaQuest(id: Long): ArenaQuestCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_ARENA_QUESTS
        qh.Columns = null
        qh.Selection = "a." + S.COLUMN_ARENA_QUESTS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArenaQuestCursor(wrapJoinHelper(builderArenaQuest(), qh))
    }

    /*
     * Get all arena quests based on location
     */
    fun queryArenaQuestLocation(id: Long): ArenaQuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARENA_QUESTS
        qh.Selection = "a." + S.COLUMN_ARENA_QUESTS_LOCATION_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArenaQuestCursor(wrapJoinHelper(builderArenaQuest(), qh))
    }

    /*
     * Helper method to query for ArenaQuest
     */
    private fun builderArenaQuest(): SQLiteQueryBuilder {
        //		SELECT a._id AS _id, a.name AS aname, a.location_id, a.reward.,
        //		a.num_participants, a.time_s, a.time_a, a.time_b,
        //		l.name AS lname
        //		FROM arena_quests AS a
        //		LEFT OUTER JOIN locations AS l on a.location_id = l._id;

        val a = "a"
        val l = "l"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = a + "." + S.COLUMN_ARENA_QUESTS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_ARENA_QUESTS_NAME] = a + "." + S.COLUMN_ARENA_QUESTS_NAME + " AS " + a + S.COLUMN_ARENA_QUESTS_NAME
        projectionMap[S.COLUMN_ARENA_QUESTS_GOAL] = a + "." + S.COLUMN_ARENA_QUESTS_GOAL
        projectionMap[S.COLUMN_ARENA_QUESTS_LOCATION_ID] = a + "." + S.COLUMN_ARENA_QUESTS_LOCATION_ID
        projectionMap[S.COLUMN_ARENA_QUESTS_REWARD] = a + "." + S.COLUMN_ARENA_QUESTS_REWARD
        projectionMap[S.COLUMN_ARENA_QUESTS_NUM_PARTICIPANTS] = a + "." + S.COLUMN_ARENA_QUESTS_NUM_PARTICIPANTS
        projectionMap[S.COLUMN_ARENA_QUESTS_TIME_S] = a + "." + S.COLUMN_ARENA_QUESTS_TIME_S
        projectionMap[S.COLUMN_ARENA_QUESTS_TIME_A] = a + "." + S.COLUMN_ARENA_QUESTS_TIME_A
        projectionMap[S.COLUMN_ARENA_QUESTS_TIME_B] = a + "." + S.COLUMN_ARENA_QUESTS_TIME_B

        projectionMap[l + S.COLUMN_LOCATIONS_NAME] = l + "." + S.COLUMN_LOCATIONS_NAME + " AS " + l + S.COLUMN_LOCATIONS_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ARENA_QUESTS + " AS a" + " LEFT OUTER JOIN " + S.TABLE_LOCATIONS +
                " AS l " + " ON " + "a." + S.COLUMN_ARENA_QUESTS_LOCATION_ID + " = " + "l." + S.COLUMN_LOCATIONS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** ARENA REWARD QUERIES *****************************************
     */

    /*
	 * Get all reward arena quests based on item
	 */
    fun queryArenaRewardItem(id: Long): ArenaRewardCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARENA_REWARDS
        qh.Selection = "ar." + S.COLUMN_ARENA_REWARDS_ITEM_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "ar." + S.COLUMN_ARENA_REWARDS_PERCENTAGE + " DESC"
        qh.Limit = null

        return ArenaRewardCursor(wrapJoinHelper(builderArenaReward(), qh))
    }

    /*
     * Get all arena quest reward items based on arena quest
     */
    fun queryArenaRewardArena(id: Long): ArenaRewardCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARENA_REWARDS
        qh.Selection = "ar." + S.COLUMN_ARENA_REWARDS_ARENA_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArenaRewardCursor(wrapJoinHelper(builderArenaReward(), qh))
    }

    /*
     * Helper method to query for ArenaReward
     */
    private fun builderArenaReward(): SQLiteQueryBuilder {
        //		SELECT ar._id AS _id, ar.arena_id, ar.item_id,
        //		ar.percentage, ar.stack_size,
        //		a.name AS aname, i.name AS iname
        //		FROM quest_rewards AS ar
        //		LEFT OUTER JOIN arena_quests AS a ON ar.arena_id = q._id
        //		LEFT OUTER JOIN items AS i ON ar.item_id = i._id;

        val ar = "ar"
        val i = "i"
        val a = "a"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = ar + "." + S.COLUMN_ARENA_REWARDS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_ARENA_REWARDS_ITEM_ID] = ar + "." + S.COLUMN_ARENA_REWARDS_ITEM_ID
        projectionMap[S.COLUMN_ARENA_REWARDS_ARENA_ID] = ar + "." + S.COLUMN_ARENA_REWARDS_ARENA_ID
        projectionMap[S.COLUMN_ARENA_REWARDS_PERCENTAGE] = ar + "." + S.COLUMN_ARENA_REWARDS_PERCENTAGE
        projectionMap[S.COLUMN_ARENA_REWARDS_STACK_SIZE] = ar + "." + S.COLUMN_ARENA_REWARDS_STACK_SIZE

        projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[a + S.COLUMN_ARENA_QUESTS_NAME] = a + "." + S.COLUMN_ARENA_QUESTS_NAME + " AS " + a + S.COLUMN_ARENA_QUESTS_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ARENA_REWARDS + " AS ar" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "ar." +
                S.COLUMN_ARENA_REWARDS_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_ARENA_QUESTS +
                " AS a " + " ON " + "ar." + S.COLUMN_ARENA_REWARDS_ARENA_ID + " = " + "a." + S.COLUMN_ARENA_QUESTS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** ARMOR QUERIES *****************************************
     */

    /*
	 * Get armor filtered by search
	 */
    fun queryArmorSearch(search: String): ArmorCursor {
        val qh = QueryHelper()
        qh.Distinct = false
        qh.Columns = null
        qh.Selection = "i." + S.COLUMN_ITEMS_NAME + " LIKE ?"
        qh.SelectionArgs = arrayOf('%'.toString() + search + '%'.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArmorCursor(wrapJoinHelper(builderArmor(), qh))
    }

    /*
     * Get all armor
     */
    fun queryArmor(): ArmorCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARMOR
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArmorCursor(wrapJoinHelper(builderArmor(), qh))
    }

    /*
     * Get a specific armor
     */
    fun queryArmor(id: Long): ArmorCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARMOR
        qh.Selection = "a." + S.COLUMN_ARMOR_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return ArmorCursor(wrapJoinHelper(builderArmor(), qh))
    }

    /*
     * Get a specific armor based on hunter type
     */
    fun queryArmorType(type: Int): ArmorCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARMOR
        qh.Selection = "a." + S.COLUMN_ARMOR_HUNTER_TYPE + " = ? " + " OR " +
                "a." + S.COLUMN_ARMOR_HUNTER_TYPE + " = 2"
        qh.SelectionArgs = arrayOf(Integer.toString(type))
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArmorCursor(wrapJoinHelper(builderArmor(), qh))
    }

    /*
     * Get a specific armor based on slot
     */
    fun queryArmorSlot(slot: String): ArmorCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARMOR
        qh.Selection = "a." + S.COLUMN_ARMOR_SLOT + " = ?"
        qh.SelectionArgs = arrayOf(slot)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArmorCursor(wrapJoinHelper(builderArmor(), qh))
    }

    /*
     * Get a specific armor based on hunter type and slot
     */
    fun queryArmorTypeSlot(type: String, slot: String): ArmorCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ARMOR
        qh.Selection = "(a." + S.COLUMN_ARMOR_HUNTER_TYPE + " = ?" + " OR " +
                "a." + S.COLUMN_ARMOR_HUNTER_TYPE + " = 'Both') " + " AND " +
                "a." + S.COLUMN_ARMOR_SLOT + " = ?"
        qh.SelectionArgs = arrayOf(type, slot)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ArmorCursor(wrapJoinHelper(builderArmor(), qh))
    }

    /*
     * Helper method to query for armor
     */
    private fun builderArmor(): SQLiteQueryBuilder {
        //		SELECT a._id AS _id, a.slot, a.defense, a.max_defense, a.fire_res, a.thunder_res,
        //		a.dragon_res, a.water_res, a.ice_res, a.gender, a.hunter_type, a.num_slots,
        //		i.name, i.jpn_name, i.type, i.rarity, i.carry_capacity, i.buy, i.sell, i.description,
        //		i.icon_name, i.armor_dupe_name_fix
        //		FROM armor AS a LEFT OUTER JOIN	items AS i ON a._id = i._id;

        val a = "a"
        val i = "i"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = a + "." + S.COLUMN_ARMOR_ID + " AS " + "_id"
        projectionMap[S.COLUMN_ARMOR_SLOT] = a + "." + S.COLUMN_ARMOR_SLOT
        projectionMap[S.COLUMN_ARMOR_DEFENSE] = a + "." + S.COLUMN_ARMOR_DEFENSE
        projectionMap[S.COLUMN_ARMOR_MAX_DEFENSE] = a + "." + S.COLUMN_ARMOR_MAX_DEFENSE
        projectionMap[S.COLUMN_ARMOR_FIRE_RES] = a + "." + S.COLUMN_ARMOR_FIRE_RES
        projectionMap[S.COLUMN_ARMOR_THUNDER_RES] = a + "." + S.COLUMN_ARMOR_THUNDER_RES
        projectionMap[S.COLUMN_ARMOR_DRAGON_RES] = a + "." + S.COLUMN_ARMOR_DRAGON_RES
        projectionMap[S.COLUMN_ARMOR_WATER_RES] = a + "." + S.COLUMN_ARMOR_WATER_RES
        projectionMap[S.COLUMN_ARMOR_ICE_RES] = a + "." + S.COLUMN_ARMOR_ICE_RES
        projectionMap[S.COLUMN_ARMOR_GENDER] = a + "." + S.COLUMN_ARMOR_GENDER
        projectionMap[S.COLUMN_ARMOR_HUNTER_TYPE] = a + "." + S.COLUMN_ARMOR_HUNTER_TYPE
        projectionMap[S.COLUMN_ARMOR_NUM_SLOTS] = a + "." + S.COLUMN_ARMOR_NUM_SLOTS
        projectionMap[S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_JPN_NAME] = i + "." + S.COLUMN_ITEMS_JPN_NAME
        projectionMap[S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY
        projectionMap[S.COLUMN_ITEMS_CARRY_CAPACITY] = i + "." + S.COLUMN_ITEMS_CARRY_CAPACITY
        projectionMap[S.COLUMN_ITEMS_BUY] = i + "." + S.COLUMN_ITEMS_BUY
        projectionMap[S.COLUMN_ITEMS_SELL] = i + "." + S.COLUMN_ITEMS_SELL
        projectionMap[S.COLUMN_ITEMS_DESCRIPTION] = i + "." + S.COLUMN_ITEMS_DESCRIPTION
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX] = i + "." + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ARMOR + " AS a" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "a." +
                S.COLUMN_ARMOR_ID + " = " + "i." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** COMBINING QUERIES *****************************************
     */

    /*
	 * Get all combinings
	 */
    fun queryCombinings(): CombiningCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_COMBINING
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return CombiningCursor(wrapJoinHelper(builderCursor(), qh))
    }

    /*
     * Get a specific combining
     */
    fun queryCombining(id: Long): CombiningCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_COMBINING
        qh.Selection = "c._id" + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return CombiningCursor(wrapJoinHelper(builderCursor(), qh))
    }

    fun queryCombinationsOnItemID(id: Long): CombiningCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_COMBINING
        qh.Selection = "crt._id" + " = ?" +
                " OR mat1._id" + " = ?" +
                " OR mat2._id" + " = ?"
        qh.SelectionArgs = arrayOf(id.toString(), id.toString(), id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return CombiningCursor(wrapJoinHelper(builderCursor(), qh))
    }

    private fun builderCursor(): SQLiteQueryBuilder {
        //		SELECT c._id AS _id, c.amount_made_min,  c.amount_made_max, c.percentage,
        //		crt._id AS crt__id, crt.name AS crt_name, crt.jpn_name AS crt_jpn_name, crt.type AS crt_type, crt.rarity AS crt_rarity,
        //		crt.carry_capacity AS crt_carry_capacity, crt.buy AS crt_buy, crt.sell AS crt_sell, crt.description AS crt_description,
        //		crt.icon_name AS crt_icon_name, crt.armor_dupe_name_fix AS crt_armor_dupe_name,
        //
        //		mat1._id AS mat1__id, mat1.name AS mat1_name, mat1.jpn_name AS mat1_jpn_name, mat1.type AS mat1_type, mat1.rarity AS mat1_rarity,
        //		mat1.carry_capacity AS mat1_carry_capacity, mat1.buy AS mat1_buy, mat1.sell AS mat1_sell, mat1.description AS mat1_description,
        //		mat1.icon_name AS mat1_icon_name, mat1.armor_dupe_name_fix AS mat1_armor_dupe_name,
        //
        //
        //		mat2._id AS mat2__id, mat2.name AS mat2_name, mat2.jpn_name AS mat2_jpn_name, mat2.type AS mat2_type, mat2.rarity AS mat2_rarity,
        //		mat2.carry_capacity AS mat2_carry_capacity, mat2.buy AS mat2_buy, mat2.sell AS mat2_sell, mat2.description AS mat2_description,
        //		mat2.icon_name AS mat2_icon_name, mat2.armor_dupe_name_fix AS mat2_armor_dupe_name
        //
        //		FROM combining AS c LEFT OUTER JOIN items AS crt ON c.created_item_id = crt._id
        //		LEFT OUTER JOIN items AS mat1 ON c.item_1_id = mat1._id
        //		LEFT OUTER JOIN items AS mat2 ON c.item_2_id = mat2._id;

        val comb = "c."
        val items = arrayOf("crt", "mat1", "mat2")

        val projectionMap = HashMap<String, String>()
        projectionMap["_id"] = comb + S.COLUMN_ITEMS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_COMBINING_AMOUNT_MADE_MIN] = comb + S.COLUMN_COMBINING_AMOUNT_MADE_MIN
        projectionMap[S.COLUMN_COMBINING_AMOUNT_MADE_MAX] = comb + S.COLUMN_COMBINING_AMOUNT_MADE_MAX
        projectionMap[S.COLUMN_COMBINING_PERCENTAGE] = comb + S.COLUMN_COMBINING_PERCENTAGE

        for (i in items) {
            projectionMap[i + S.COLUMN_ITEMS_ID] = i + "." + S.COLUMN_ITEMS_ID + " AS " + i + S.COLUMN_ITEMS_ID

            projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
            projectionMap[i + S.COLUMN_ITEMS_JPN_NAME] = i + "." + S.COLUMN_ITEMS_JPN_NAME + " AS " + i + S.COLUMN_ITEMS_JPN_NAME
            projectionMap[i + S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE + " AS " + i + S.COLUMN_ITEMS_TYPE
            projectionMap[i + S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE + " AS " + i + S.COLUMN_ITEMS_SUB_TYPE
            projectionMap[i + S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY + " AS " + i + S.COLUMN_ITEMS_RARITY
            projectionMap[i + S.COLUMN_ITEMS_CARRY_CAPACITY] = i + "." + S.COLUMN_ITEMS_CARRY_CAPACITY + " AS " + i + S.COLUMN_ITEMS_CARRY_CAPACITY
            projectionMap[i + S.COLUMN_ITEMS_BUY] = i + "." + S.COLUMN_ITEMS_BUY + " AS " + i + S.COLUMN_ITEMS_BUY
            projectionMap[i + S.COLUMN_ITEMS_SELL] = i + "." + S.COLUMN_ITEMS_SELL + " AS " + i + S.COLUMN_ITEMS_SELL
            projectionMap[i + S.COLUMN_ITEMS_DESCRIPTION] = i + "." + S.COLUMN_ITEMS_DESCRIPTION + " AS " + i + S.COLUMN_ITEMS_DESCRIPTION
            projectionMap[i + S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME + " AS " + i + S.COLUMN_ITEMS_ICON_NAME
            projectionMap[i + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX] = i + "." + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX + " AS " + i + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX
        }

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_COMBINING + " AS c" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS crt" + " ON " + "c." +
                S.COLUMN_COMBINING_CREATED_ITEM_ID + " = " + "crt." + S.COLUMN_ITEMS_ID +
                " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS mat1" + " ON " + "c." +
                S.COLUMN_COMBINING_ITEM_1_ID + " = " + "mat1." + S.COLUMN_ITEMS_ID +
                " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS mat2" + " ON " + "c." +
                S.COLUMN_COMBINING_ITEM_2_ID + " = " + "mat2." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB

    }

    /**
     * ****************************** COMPONENT QUERIES *****************************************
     */

    /*
	 * Get all components for a created item
	 */
    fun queryComponentCreated(id: Long): ComponentCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_COMPONENTS
        qh.Selection = "c." + S.COLUMN_COMPONENTS_CREATED_ITEM_ID + " = ? "
        //s" AND " + "c." + S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID + " < " + S.SECTION_ARMOR;
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "c.type"
        qh.Limit = null

        return ComponentCursor(wrapJoinHelper(builderComponent(), qh))
    }

    /*
     * Get all components for a component item
     */
    fun queryComponentComponent(id: Long): ComponentCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_COMPONENTS
        qh.Selection = "c." + S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ComponentCursor(wrapJoinHelper(builderComponent(), qh))
    }

    /*
     * Get all components for a created item and type
     */
    fun queryComponentCreatedType(id: Long, type: String): ComponentCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_COMPONENTS
        qh.Selection = "c." + S.COLUMN_COMPONENTS_CREATED_ITEM_ID + " = ? " +
                " AND " + "c." + S.COLUMN_COMPONENTS_TYPE + " = ?"
        qh.SelectionArgs = arrayOf("" + id, type)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ComponentCursor(wrapJoinHelper(builderComponent(), qh))
    }

    /*
     * Helper method to query for component
     */
    private fun builderComponent(): SQLiteQueryBuilder {
        //		SELECT c._id AS _id, c.created_item_id, c.component_item_id,
        //		c.quantity, c.type, cr.name AS crname, co.name AS coname
        //		FROM components AS c
        //		LEFT OUTER JOIN items AS cr ON c.created_item_id = cr._id
        //		LEFT OUTER JOIN items AS co ON c.component_item_id = co._id;

        val c = "c"
        val cr = "cr"
        val co = "co"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = c + "." + S.COLUMN_COMPONENTS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_COMPONENTS_CREATED_ITEM_ID] = c + "." + S.COLUMN_COMPONENTS_CREATED_ITEM_ID
        projectionMap[S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID] = c + "." + S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID
        projectionMap[S.COLUMN_COMPONENTS_QUANTITY] = c + "." + S.COLUMN_COMPONENTS_QUANTITY
        projectionMap[S.COLUMN_COMPONENTS_TYPE] = c + "." + S.COLUMN_COMPONENTS_TYPE

        projectionMap[cr + S.COLUMN_ITEMS_NAME] = cr + "." + S.COLUMN_ITEMS_NAME + " AS " + cr + S.COLUMN_ITEMS_NAME
        projectionMap[cr + S.COLUMN_ITEMS_TYPE] = cr + "." + S.COLUMN_ITEMS_TYPE + " AS " + cr + S.COLUMN_ITEMS_TYPE
        projectionMap[cr + S.COLUMN_ITEMS_SUB_TYPE] = cr + "." + S.COLUMN_ITEMS_SUB_TYPE + " AS " + cr + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[cr + S.COLUMN_ITEMS_RARITY] = cr + "." + S.COLUMN_ITEMS_RARITY + " AS " + cr + S.COLUMN_ITEMS_RARITY
        projectionMap[cr + S.COLUMN_ITEMS_ICON_NAME] = cr + "." + S.COLUMN_ITEMS_ICON_NAME + " AS " + cr + S.COLUMN_ITEMS_ICON_NAME

        projectionMap[co + S.COLUMN_ITEMS_NAME] = co + "." + S.COLUMN_ITEMS_NAME + " AS " + co + S.COLUMN_ITEMS_NAME
        projectionMap[co + S.COLUMN_ITEMS_TYPE] = co + "." + S.COLUMN_ITEMS_TYPE + " AS " + co + S.COLUMN_ITEMS_TYPE
        projectionMap[co + S.COLUMN_ITEMS_ICON_NAME] = co + "." + S.COLUMN_ITEMS_ICON_NAME + " AS " + co + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[co + S.COLUMN_ITEMS_SUB_TYPE] = co + "." + S.COLUMN_ITEMS_SUB_TYPE + " AS " + co + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[co + S.COLUMN_ITEMS_RARITY] = co + "." + S.COLUMN_ITEMS_RARITY + " AS " + co + S.COLUMN_ITEMS_RARITY


        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_COMPONENTS + " AS c" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS cr" + " ON " + "c." +
                S.COLUMN_COMPONENTS_CREATED_ITEM_ID + " = " + "cr." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_ITEMS +
                " AS co " + " ON " + "c." + S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID + " = " + "co." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** DECORATION QUERIES *****************************************
     */

    /*
	 * Get all decorations
	 */
    fun queryDecorations(): DecorationCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_DECORATIONS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "skill_1_name ASC"
        qh.Limit = null

        return DecorationCursor(wrapJoinHelper(builderDecoration(), qh))
    }

    /*
     * Get decorations filtered by a search term
     */
    fun queryDecorationsSearch(searchTerm: String): DecorationCursor {
        var searchTerm = searchTerm
        searchTerm = '%'.toString() + searchTerm + '%'.toString()

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_DECORATIONS
        qh.Selection = "i.name LIKE ? OR skill_1_name LIKE ? OR skill_2_name LIKE ?"
        qh.SelectionArgs = arrayOf(searchTerm, searchTerm, searchTerm)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "skill_1_name ASC"
        qh.Limit = null

        return DecorationCursor(wrapJoinHelper(builderDecoration(), qh))
    }

    /*
     * Get a specific decoration
     */
    fun queryDecoration(id: Long): DecorationCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_DECORATIONS
        qh.Selection = "i._id" + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return DecorationCursor(wrapJoinHelper(builderDecoration(), qh))
    }

    /*
     * Helper method to query for decorations
     */
    private fun builderDecoration(): SQLiteQueryBuilder {
        //		 SELECT i._id AS item_id, i.name, i.jpn_name, i.type, i.rarity, i.carry_capacity, i.buy, i.sell, i.description,
        //		 i.icon_name, i.armor_dupe_name_fix, d.num_slots, s1._id AS skill_1_id, s1.name AS skill_1_name, its1.point_value
        //		 AS skill_1_point, s2._id AS skill_1_id, s2.name AS skill_2_name, its2.point_value AS skill_2_point
        //		 FROM decorations AS d LEFT OUTER JOIN items AS i ON d._id = i._id
        //		 LEFT OUTER JOIN item_to_skill_tree AS its1 ON i._id = its1.item_id and its1.point_value > 0
        //		 LEFT OUTER JOIN skill_trees AS s1 ON its1.skill_tree_id = s1._id
        //		 LEFT OUTER JOIN item_to_skill_tree AS its2 ON i._id = its2.item_id and s1._id != its2.skill_tree_id
        //		 LEFT OUTER JOIN skill_trees AS s2 ON its2.skill_tree_id = s2._id;

        val projectionMap = HashMap<String, String>()
        projectionMap["_id"] = "i." + S.COLUMN_ITEMS_ID + " AS " + "_id"
        projectionMap["item_name"] = "i." + S.COLUMN_ITEMS_NAME + " AS " + "item_name"
        projectionMap[S.COLUMN_ITEMS_JPN_NAME] = "i." + S.COLUMN_ITEMS_JPN_NAME
        projectionMap[S.COLUMN_ITEMS_TYPE] = "i." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = "i." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = "i." + S.COLUMN_ITEMS_RARITY
        projectionMap[S.COLUMN_ITEMS_CARRY_CAPACITY] = "i." + S.COLUMN_ITEMS_CARRY_CAPACITY
        projectionMap[S.COLUMN_ITEMS_BUY] = "i." + S.COLUMN_ITEMS_BUY
        projectionMap[S.COLUMN_ITEMS_SELL] = "i." + S.COLUMN_ITEMS_SELL
        projectionMap[S.COLUMN_ITEMS_DESCRIPTION] = "i." + S.COLUMN_ITEMS_DESCRIPTION
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = "i." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX] = "i." + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX
        projectionMap[S.COLUMN_DECORATIONS_NUM_SLOTS] = "d." + S.COLUMN_DECORATIONS_NUM_SLOTS
        projectionMap["skill_1_id"] = "s1." + S.COLUMN_SKILL_TREES_ID + " AS " + "skill_1_id"
        projectionMap["skill_1_name"] = "s1." + S.COLUMN_SKILL_TREES_NAME + " AS " + "skill_1_name"
        projectionMap["skill_1_point_value"] = "its1." + S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE + " AS " + "skill_1_point_value"
        projectionMap["skill_2_id"] = "s2." + S.COLUMN_SKILL_TREES_ID + " AS " + "skill_2_id"
        projectionMap["skill_2_name"] = "s2." + S.COLUMN_SKILL_TREES_NAME + " AS " + "skill_2_name"
        projectionMap["skill_2_point_value"] = "its2." + S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE + " AS " + "skill_2_point_value"

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_DECORATIONS + " AS d" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "d." +
                S.COLUMN_DECORATIONS_ID + " = " + "i." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_ITEM_TO_SKILL_TREE +
                " AS its1 " + " ON " + "i." + S.COLUMN_ITEMS_ID + " = " + "its1." + S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID + " AND " +
                "its1." + S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE + " > 0 " + " LEFT OUTER JOIN " + S.TABLE_SKILL_TREES + " AS s1" +
                " ON " + "its1." + S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID + " = " + "s1." + S.COLUMN_SKILL_TREES_ID +
                " LEFT OUTER JOIN " + S.TABLE_ITEM_TO_SKILL_TREE + " AS its2 " + " ON " + "i." + S.COLUMN_ITEMS_ID + " = " +
                "its2." + S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID + " AND " + "s1." + S.COLUMN_SKILL_TREES_ID + " != " +
                "its2." + S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID + " LEFT OUTER JOIN " + S.TABLE_SKILL_TREES + " AS s2" +
                " ON " + "its2." + S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID + " = " + "s2." + S.COLUMN_SKILL_TREES_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** GATHERING QUERIES *****************************************
     */

    /*
	 * Get all gathering locations based on item
	 */
    fun queryGatheringItem(id: Long): GatheringCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_GATHERING
        qh.Selection = "g." + S.COLUMN_GATHERING_ITEM_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = ("g." + S.COLUMN_GATHERING_RANK + " DESC, " + "l." + S.COLUMN_LOCATIONS_MAP
                + " ASC")
        qh.Limit = null

        return GatheringCursor(wrapJoinHelper(builderGathering(), qh))
    }

    /*
     * Get all gathering items based on location
     */
    fun queryGatheringLocation(id: Long): GatheringCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_GATHERING
        qh.Selection = "g." + S.COLUMN_GATHERING_LOCATION_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return GatheringCursor(wrapJoinHelper(builderGathering(), qh))
    }

    /*
     * Get all gathering items based on location and rank
     */
    fun queryGatheringLocationRank(id: Long, rank: String): GatheringCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_GATHERING
        qh.Selection = "g." + S.COLUMN_GATHERING_LOCATION_ID + " = ? " + "AND " +
                "g." + S.COLUMN_GATHERING_RANK + " = ? "
        qh.SelectionArgs = arrayOf("" + id, rank)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return GatheringCursor(wrapJoinHelper(builderGathering(), qh))
    }

    /*
     * Helper method to query for Gathering
     */
    private fun builderGathering(): SQLiteQueryBuilder {
        //		SELECT g._id AS _id, g.item_id, g.location_id, g.area,
        //		g.site, g.site_set, g.site_set_percentage,
        //		g.site_set_gathers_min, g.site_set_gathers_max, g.rank,
        //		g.percentage, i.name AS iname, l.name AS lname
        //		FROM gathering AS g
        //		LEFT OUTER JOIN items AS i ON g.item_id = i._id
        //		LEFT OUTER JOIN locations AS l on g.location_id = l._id;

        val g = "g"
        val i = "i"
        val l = "l"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = g + "." + S.COLUMN_GATHERING_ID + " AS " + "_id"
        projectionMap[S.COLUMN_GATHERING_ITEM_ID] = g + "." + S.COLUMN_GATHERING_ITEM_ID
        projectionMap[S.COLUMN_GATHERING_LOCATION_ID] = g + "." + S.COLUMN_GATHERING_LOCATION_ID
        projectionMap[S.COLUMN_GATHERING_AREA] = g + "." + S.COLUMN_GATHERING_AREA
        projectionMap[S.COLUMN_GATHERING_SITE] = g + "." + S.COLUMN_GATHERING_SITE
        projectionMap[S.COLUMN_GATHERING_RANK] = g + "." + S.COLUMN_GATHERING_RANK
        projectionMap[S.COLUMN_GATHERING_RATE] = g + "." + S.COLUMN_GATHERING_RATE
        projectionMap[S.COLUMN_GATHERING_GROUP] = g + "." + S.COLUMN_GATHERING_GROUP
        projectionMap[S.COLUMN_GATHERING_FIXED] = g + "." + S.COLUMN_GATHERING_FIXED
        projectionMap[S.COLUMN_GATHERING_RARE] = g + "." + S.COLUMN_GATHERING_RARE
        projectionMap[S.COLUMN_GATHERING_QUANTITY] = g + "." + S.COLUMN_GATHERING_QUANTITY

        projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[l + S.COLUMN_LOCATIONS_NAME] = l + "." + S.COLUMN_LOCATIONS_NAME + " AS " + l + S.COLUMN_LOCATIONS_NAME
        projectionMap[l + S.COLUMN_LOCATIONS_MAP] = l + "." + S.COLUMN_LOCATIONS_MAP + " AS " + l + S.COLUMN_LOCATIONS_MAP

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_GATHERING + " AS g" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "g." +
                S.COLUMN_GATHERING_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_LOCATIONS +
                " AS l " + " ON " + "g." + S.COLUMN_GATHERING_LOCATION_ID + " = " + "l." + S.COLUMN_LOCATIONS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** HUNTING REWARD QUERIES *****************************************
     */

    /*
	 * Get all hunting reward monsters based on item
	 */
    fun queryHuntingRewardItem(id: Long): HuntingRewardCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_HUNTING_REWARDS
        qh.Selection = "h." + S.COLUMN_HUNTING_REWARDS_ITEM_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "m." + S.COLUMN_MONSTERS_ID + " ASC, " + "h." + S.COLUMN_HUNTING_REWARDS_RANK +
                " DESC, " + "h." + S.COLUMN_HUNTING_REWARDS_ID + " ASC"
        qh.Limit = null

        return HuntingRewardCursor(wrapJoinHelper(builderHuntingReward(), qh))
    }

    /*
     * Get all hunting reward items based on monster
     */
    fun queryHuntingRewardMonster(ids: LongArray): HuntingRewardCursor {

        val string_list = arrayOfNulls<String>(ids.size)
        for (i in ids.indices) {
            string_list[i] = ids[i].toString()
        }

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_HUNTING_REWARDS
        qh.Selection = "h." + S.COLUMN_HUNTING_REWARDS_MONSTER_ID + " IN (" + makePlaceholders(ids.size) + ")"
        qh.SelectionArgs = string_list
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return HuntingRewardCursor(wrapJoinHelper(builderHuntingReward(), qh))
    }

    /*
     * Get all hunting reward items based on monster and rank
     */
    fun queryHuntingRewardMonsterRank(ids: LongArray, rank: String): HuntingRewardCursor {

        val string_list = arrayOfNulls<String>(ids.size + 1)
        for (i in ids.indices) {
            string_list[i] = ids[i].toString()
        }
        string_list[ids.size] = rank

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_HUNTING_REWARDS
        qh.Selection = ("h." + S.COLUMN_HUNTING_REWARDS_MONSTER_ID + " IN (" + makePlaceholders(ids.size) + ")"
                + " AND " + "h." + S.COLUMN_HUNTING_REWARDS_RANK + " = ? ")
        qh.SelectionArgs = string_list
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return HuntingRewardCursor(wrapJoinHelper(builderHuntingReward(), qh))
    }

    /*
     * Helper method to query for HuntingReward
     */
    private fun builderHuntingReward(): SQLiteQueryBuilder {
        //		SELECT h._id AS _id, h.item_id, h.monster_id,
        //		h.condition, h.rank, h.stack_size, h.percentage,
        //		i.name AS iname, m.name AS mname
        //		FROM hunting_rewards AS h
        //		LEFT OUTER JOIN items AS i ON h.item_id = i._id
        //		LEFT OUTER JOIN monsters AS m ON h.monster_id = m._id;

        val h = "h"
        val i = "i"
        val m = "m"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = h + "." + S.COLUMN_HUNTING_REWARDS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_HUNTING_REWARDS_ITEM_ID] = h + "." + S.COLUMN_HUNTING_REWARDS_ITEM_ID
        projectionMap[S.COLUMN_HUNTING_REWARDS_MONSTER_ID] = h + "." + S.COLUMN_HUNTING_REWARDS_MONSTER_ID
        projectionMap[S.COLUMN_HUNTING_REWARDS_CONDITION] = h + "." + S.COLUMN_HUNTING_REWARDS_CONDITION
        projectionMap[S.COLUMN_HUNTING_REWARDS_RANK] = h + "." + S.COLUMN_HUNTING_REWARDS_RANK
        projectionMap[S.COLUMN_HUNTING_REWARDS_STACK_SIZE] = h + "." + S.COLUMN_HUNTING_REWARDS_STACK_SIZE
        projectionMap[S.COLUMN_HUNTING_REWARDS_PERCENTAGE] = h + "." + S.COLUMN_HUNTING_REWARDS_PERCENTAGE

        projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
        projectionMap[i + S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME + " AS " + i + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[m + S.COLUMN_MONSTERS_NAME] = m + "." + S.COLUMN_MONSTERS_NAME + " AS " + m + S.COLUMN_MONSTERS_NAME
        projectionMap[m + S.COLUMN_MONSTERS_FILE_LOCATION] = m + "." + S.COLUMN_MONSTERS_FILE_LOCATION + " AS " + m + S.COLUMN_MONSTERS_FILE_LOCATION

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_HUNTING_REWARDS + " AS h" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "h." +
                S.COLUMN_HUNTING_REWARDS_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_MONSTERS +
                " AS m " + " ON " + "h." + S.COLUMN_HUNTING_REWARDS_MONSTER_ID + " = " + "m." + S.COLUMN_MONSTERS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** ITEM QUERIES *****************************************
     */

    /*
	 * Get all items
	 */
    fun queryItems(): ItemCursor {
        // SELECT DISTINCT * FROM items ORDER BY _id

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_ITEMS
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = S.COLUMN_ITEMS_ID
        qh.Limit = null

        return ItemCursor(wrapHelper(qh))
    }

    /*
     * Get a specific item
     */
    fun queryItem(id: Long): ItemCursor {
        // "SELECT DISTINCT * FROM items WHERE _id = id LIMIT 1"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_ITEMS
        qh.Columns = null
        qh.Selection = S.COLUMN_ITEMS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return ItemCursor(wrapHelper(qh))
    }

    /*
     * Get items based on search text
     */
    fun queryItemSearch(search: String): ItemCursor {
        // "SELECT * FROM items
        //  WHERE (name LIKE '% word%' OR name LIKE 'word%')
        //    AND (name LIKE '% word2%' OR name LIKE 'word2%')
        //  GROUP BY name"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_ITEMS
        qh.Columns = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        modifyQueryForSearch(qh, S.COLUMN_ITEMS_NAME, search)

        return ItemCursor(wrapHelper(qh))
    }


    /**
     * ****************************** ITEM TO SKILL TREE QUERIES *****************************************
     */

    /*
	 * Get all skills based on item
	 */
    fun queryItemToSkillTreeItem(id: Long): ItemToSkillTreeCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Selection = "itst." + S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ItemToSkillTreeCursor(wrapJoinHelper(builderItemToSkillTree(), qh))
    }

    /*
     * Get all items based on skill tree
     */
    fun queryItemToSkillTreeSkillTree(id: Long, type: String): ItemToSkillTreeCursor {

        var queryType = ""
        if (type == "Decoration") {
            queryType = "i." + S.COLUMN_ITEMS_TYPE
        } else {
            queryType = "a." + S.COLUMN_ARMOR_SLOT
        }

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ITEM_TO_SKILL_TREE
        qh.Selection = "itst." + S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID + " = ? " + " AND " +
                queryType + " = ? "
        qh.SelectionArgs = arrayOf("" + id, type)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ItemToSkillTreeCursor(wrapJoinHelper(builderItemToSkillTree(), qh))
    }

    /*
     * Helper method to query for ItemToSkillTree
     */
    private fun builderItemToSkillTree(): SQLiteQueryBuilder {
        //		SELECT itst._id AS _id, itst.item_id, itst.skill_tree_id,
        //		itst.point_value, i.name AS iname, s.name AS sname
        //		FROM item_to_skill_tree AS itst
        //		LEFT OUTER JOIN items AS i ON itst.item_id = i._id
        //		LEFT OUTER JOIN skill_trees AS s ON itst.skill_tree_id = s._id;
        //		LEFT OUTER JOIN armor AS a ON i._id = a._id
        //		LEFT OUTER JOIN decorations AS d ON i._id = d._id;

        val itst = "itst"
        val i = "i"
        val s = "s"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = itst + "." + S.COLUMN_ITEM_TO_SKILL_TREE_ID + " AS " + "_id"
        projectionMap[S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID] = itst + "." + S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID
        projectionMap[S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID] = itst + "." + S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID
        projectionMap[S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE] = itst + "." + S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE

        projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY
        projectionMap[s + S.COLUMN_SKILL_TREES_NAME] = s + "." + S.COLUMN_SKILL_TREES_NAME + " AS " + s + S.COLUMN_SKILL_TREES_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ITEM_TO_SKILL_TREE + " AS itst" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "itst." +
                S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_SKILL_TREES +
                " AS s " + " ON " + "itst." + S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID + " = " + "s." + S.COLUMN_SKILL_TREES_ID +
                " LEFT OUTER JOIN " + S.TABLE_ARMOR + " AS a" + " ON " + "i." + S.COLUMN_ITEMS_ID + " = " + "a." + S.COLUMN_ARMOR_ID +
                " LEFT OUTER JOIN " + S.TABLE_DECORATIONS + " AS d" + " ON " + "i." + S.COLUMN_ITEMS_ID + " = " + "d." +
                S.COLUMN_DECORATIONS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** ITEM TO MATERIAL QUERIES *****************************************
     */

    fun queryItemsForMaterial(material_item_id: Long): ItemToMaterialCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Selection = "itm." + S.COLUMN_ITEM_TO_MATERIAL_MATERIAL_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + material_item_id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "itm.amount DESC"
        qh.Limit = null

        return ItemToMaterialCursor(wrapJoinHelper(builderItemToMaterial(), qh))
    }

    private fun builderItemToMaterial(): SQLiteQueryBuilder {

        val itm = "itm"
        val i = "i"

        val projectionMap = HashMap<String, String>()

        //Material Mapping
        projectionMap[S.COLUMN_ITEM_TO_MATERIAL_ID] = itm + "." + S.COLUMN_ITEM_TO_MATERIAL_ID
        projectionMap[S.COLUMN_ITEM_TO_MATERIAL_ITEM_ID] = itm + "." + S.COLUMN_ITEM_TO_MATERIAL_ITEM_ID
        projectionMap[S.COLUMN_ITEM_TO_MATERIAL_AMOUNT] = itm + "." + S.COLUMN_ITEM_TO_MATERIAL_AMOUNT
        projectionMap[S.COLUMN_ITEM_TO_MATERIAL_MATERIAL_ID] = itm + "." + S.COLUMN_ITEM_TO_MATERIAL_MATERIAL_ID

        //Item
        projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ITEM_TO_MATERIAL + " AS itm" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "itm." +
                S.COLUMN_ITEM_TO_MATERIAL_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** LOCATION QUERIES *****************************************
     */

    /*
	 * Get all locations
	 */
    fun queryLocations(): LocationCursor {
        // "SELECT DISTINCT * FROM locations GROUP BY name"

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_LOCATIONS
        qh.Columns = null
        qh.Selection = "_id<100"
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        //Night versions have an _id + 100, so to keep them together we need to modify the sort.
        qh.OrderBy = null//"CASE WHEN _id>100 THEN _id-100 ELSE _id END";
        qh.Limit = null

        return LocationCursor(wrapHelper(qh))
    }

    /*
     * Get a specific location
     */
    fun queryLocation(id: Long): LocationCursor {
        // "SELECT DISTINCT * FROM locations WHERE _id = id LIMIT 1"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_LOCATIONS
        qh.Columns = null
        qh.Selection = S.COLUMN_LOCATIONS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return LocationCursor(wrapHelper(qh))
    }

    /**
     * ***************************** HORN MELODIES QUERIES **********************************************
     */

    /*
     * Get all melodies available from a given set of notes
     */
    fun queryMelodiesFromNotes(notes: String): HornMelodiesCursor {
        // "SELECT * FROM horn_melodies WHERE notes = notes"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_HORN_MELODIES
        qh.Columns = null
        qh.Selection = S.COLUMN_HORN_MELODIES_NOTES + " = ?"
        qh.SelectionArgs = arrayOf(notes)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return HornMelodiesCursor(wrapHelper(qh))
    }

    /******************************** MONSTER AILMENT QUERIES  */
    /* Get all ailments a from a particular monster */
    fun queryAilmentsFromMonster(id: Long): MonsterAilmentCursor {
        // SELECT * FROM monster_ailment WHERE monster_id = id

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_AILMENT
        qh.Columns = null
        qh.Selection = S.COLUMN_AILMENT_MONSTER_ID + " = " + id
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterAilmentCursor(wrapHelper(qh))
    }

    /********************************* MONSTER HABITAT QUERIES  */

    /**
     * Get a cursor with a query to grab all habitats of a monster
     *
     * @param id id of the monster to query
     * @return A habitat cursor
     */
    fun queryHabitatMonster(id: Long): MonsterHabitatCursor {
        // Select * FROM monster_habitat WHERE monster_id = id
        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_HABITAT
        qh.Columns = null
        qh.Selection = S.COLUMN_HABITAT_MONSTER_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterHabitatCursor(wrapJoinHelper(builderHabitat(qh.Distinct), qh))
    }

    /**
     * Get a cursor with a query to grab all monsters by a location
     *
     * @param id id of the location to query
     * @return A habitat cursor
     */
    fun queryHabitatLocation(id: Long): MonsterHabitatCursor {
        // Select * FROM monster_habitat WHERE location_id = id
        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_HABITAT
        qh.Columns = null
        qh.Selection = S.COLUMN_HABITAT_LOCATION_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "m" + S.COLUMN_MONSTERS_SORT_NAME + " ASC"
        qh.Limit = null

        return MonsterHabitatCursor(wrapJoinHelper(builderHabitat(qh.Distinct), qh))
    }

    /*
 * Helper method to query for Habitat/Monster/Location
 */
    private fun builderHabitat(Distinct: Boolean): SQLiteQueryBuilder {
        val h = "h"
        val m = "m"
        val l = "l"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = h + "." + S.COLUMN_HABITAT_ID + " AS " + "_id"
        projectionMap["start_area"] = h + "." + S.COLUMN_HABITAT_START + " AS " + "start_area"
        projectionMap["move_area"] = h + "." + S.COLUMN_HABITAT_AREAS + " AS " + "move_area"
        projectionMap["rest_area"] = h + "." + S.COLUMN_HABITAT_REST + " AS " + "rest_area"

        projectionMap[l + S.COLUMN_LOCATIONS_ID] = l + "." + S.COLUMN_LOCATIONS_ID + " AS " + l + S.COLUMN_LOCATIONS_ID
        projectionMap[l + S.COLUMN_LOCATIONS_NAME] = l + "." + S.COLUMN_LOCATIONS_NAME + " AS " + l + S.COLUMN_LOCATIONS_NAME
        projectionMap[l + S.COLUMN_LOCATIONS_MAP] = l + "." + S.COLUMN_LOCATIONS_MAP + " AS " + l + S.COLUMN_LOCATIONS_MAP

        projectionMap[m + S.COLUMN_MONSTERS_ID] = m + "." + S.COLUMN_MONSTERS_ID + " AS " + m + S.COLUMN_MONSTERS_ID
        projectionMap[m + S.COLUMN_MONSTERS_SORT_NAME] = m + "." + S.COLUMN_MONSTERS_SORT_NAME + " AS " + m + S.COLUMN_MONSTERS_SORT_NAME
        projectionMap[m + S.COLUMN_MONSTERS_NAME] = m + "." + S.COLUMN_MONSTERS_NAME + " AS " + m + S.COLUMN_MONSTERS_NAME
        projectionMap[m + S.COLUMN_MONSTERS_CLASS] = m + "." + S.COLUMN_MONSTERS_CLASS + " AS " + m + S.COLUMN_MONSTERS_CLASS
        projectionMap[m + S.COLUMN_MONSTERS_FILE_LOCATION] = m + "." + S.COLUMN_MONSTERS_FILE_LOCATION + " AS " + m + S.COLUMN_MONSTERS_FILE_LOCATION

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_HABITAT + " AS h" + " LEFT OUTER JOIN " + S.TABLE_MONSTERS + " AS m" + " ON " + "h." +
                S.COLUMN_HABITAT_MONSTER_ID + " = " + "m." + S.COLUMN_MONSTERS_ID + " LEFT OUTER JOIN " + S.TABLE_LOCATIONS +
                " AS l " + " ON " + "h." + S.COLUMN_HABITAT_LOCATION_ID + " = " + "l." + S.COLUMN_LOCATIONS_ID

        QB.setDistinct(Distinct)
        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** MONSTER STATUS QUERIES *****************************************
     */

    /*
	 * Get all monster status info for a monster
	 * @param id The monster id
	 */
    fun queryMonsterStatus(id: Long): MonsterStatusCursor {
        // "SELECT * FROM monster_status WHERE monster_id = id"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_MONSTER_STATUS
        qh.Columns = null
        qh.Selection = S.COLUMN_MONSTER_STATUS_MONSTER_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterStatusCursor(wrapHelper(qh))
    }

    /**
     * ****************************** MONSTER DAMAGE QUERIES *****************************************
     */

    /*
	 * Get all monster damage for a monster
	 */
    fun queryMonsterDamage(id: Long): MonsterDamageCursor {
        // "SELECT * FROM monster_damage WHERE monster_id = id"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_MONSTER_DAMAGE
        qh.Columns = null
        qh.Selection = S.COLUMN_MONSTER_DAMAGE_MONSTER_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterDamageCursor(wrapHelper(qh))
    }

    /**
     * ****************************** MONSTER TO ARENA QUERIES *****************************************
     */

    /*
	 * Get all arena quests based on monster
	 */
    fun queryMonsterToArenaMonster(id: Long): MonsterToArenaCursor {

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_MONSTER_TO_ARENA
        qh.Columns = null
        qh.Selection = "mta." + S.COLUMN_MONSTER_TO_ARENA_MONSTER_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = "a." + S.COLUMN_ARENA_QUESTS_NAME
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterToArenaCursor(wrapJoinHelper(builderMonsterToArena(qh.Distinct), qh))
    }

    /*
     * Get all monsters based on arena quest
     */
    fun queryMonsterToArenaArena(id: Long): MonsterToArenaCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_MONSTER_TO_ARENA
        qh.Columns = null
        qh.Selection = "mta." + S.COLUMN_MONSTER_TO_ARENA_ARENA_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterToArenaCursor(wrapJoinHelper(builderMonsterToArena(qh.Distinct), qh))
    }

    /*
     * Helper method to query for MonsterToArena
     */
    private fun builderMonsterToArena(Distinct: Boolean): SQLiteQueryBuilder {
        //		SELECT mta._id AS _id, mta.monster_id, mta.arena_id,
        //		m.name AS mname, a.name AS aname,
        //		FROM monster_to_arena AS mta
        //		LEFT OUTER JOIN monsters AS m ON mta.monster_id = m._id
        //		LEFT OUTER JOIN arena_quests AS a ON mta.arena_id = a._id;

        val mta = "mta"
        val m = "m"
        val a = "a"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = mta + "." + S.COLUMN_MONSTER_TO_ARENA_ID + " AS " + "_id"

        projectionMap[S.COLUMN_MONSTER_TO_ARENA_ID] = mta + "." + S.COLUMN_MONSTER_TO_ARENA_ID
        projectionMap[S.COLUMN_MONSTER_TO_ARENA_MONSTER_ID] = mta + "." + S.COLUMN_MONSTER_TO_ARENA_MONSTER_ID
        projectionMap[S.COLUMN_MONSTER_TO_ARENA_ARENA_ID] = mta + "." + S.COLUMN_MONSTER_TO_ARENA_ARENA_ID

        projectionMap[m + S.COLUMN_MONSTERS_NAME] = m + "." + S.COLUMN_MONSTERS_NAME + " AS " + m + S.COLUMN_MONSTERS_NAME
        projectionMap[S.COLUMN_MONSTERS_FILE_LOCATION] = m + "." + S.COLUMN_MONSTERS_FILE_LOCATION
        projectionMap[a + S.COLUMN_ARENA_QUESTS_NAME] = a + "." + S.COLUMN_ARENA_QUESTS_NAME + " AS " + a + S.COLUMN_ARENA_QUESTS_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_MONSTER_TO_ARENA + " AS mta" + " LEFT OUTER JOIN " + S.TABLE_MONSTERS + " AS m" + " ON " + "mta." +
                S.COLUMN_MONSTER_TO_ARENA_MONSTER_ID + " = " + "m." + S.COLUMN_MONSTERS_ID + " LEFT OUTER JOIN " + S.TABLE_ARENA_QUESTS +
                " AS a " + " ON " + "mta." + S.COLUMN_MONSTER_TO_ARENA_ARENA_ID + " = " + "a." + S.COLUMN_ARENA_QUESTS_ID

        QB.setDistinct(Distinct)
        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** MONSTER TO QUEST QUERIES *****************************************
     */

    /*
	 * Get all quests based on monster
	 */
    fun queryMonsterToQuestMonster(id: Long): MonsterToQuestCursor {

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_MONSTER_TO_QUEST
        qh.Columns = null
        qh.Selection = "mtq." + S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        //JOE: Order them specifically Village - Guild - Permit - (Any others in alphabetical order by concating them with 3)
        qh.OrderBy = "CASE q." + S.COLUMN_QUESTS_HUB + " WHEN 'Village' THEN 0 WHEN 'Guild' THEN 1 WHEN 'Permit' THEN 2 ELSE (3||q." + S.COLUMN_QUESTS_HUB + ") END, " + "q." + S.COLUMN_QUESTS_STARS + " ASC"
        qh.Limit = null

        return MonsterToQuestCursor(wrapJoinHelper(builderMonsterToQuest(qh.Distinct), qh))
    }

    /*
     * Get all monsters based on quest
     */
    fun queryMonsterToQuestQuest(id: Long): MonsterToQuestCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_MONSTER_TO_QUEST
        qh.Columns = null
        qh.Selection = "mtq." + S.COLUMN_MONSTER_TO_QUEST_QUEST_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterToQuestCursor(wrapJoinHelper(builderMonsterToQuestWithHabitats(qh.Distinct), qh))
    }

    /*
     * Helper method to query for MonsterToQuest
     */
    private fun builderMonsterToQuest(Distinct: Boolean): SQLiteQueryBuilder {
        //		SELECT mtq._id AS _id, mtq.monster_id, mtq.quest_id,
        //		mtq.unstable, m.name AS mname, q.name AS qname,
        //		q.hub, q.stars
        //		FROM monster_to_quest AS mtq
        //		LEFT OUTER JOIN monsters AS m ON mtq.monster_id = m._id
        //		LEFT OUTER JOIN quests AS q ON mtq.quest_id = q._id;

        val mtq = "mtq"
        val m = "m"
        val q = "q"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_ID + " AS " + "_id"

        projectionMap[S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID
        projectionMap[S.COLUMN_MONSTER_TO_QUEST_QUEST_ID] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_QUEST_ID
        projectionMap[S.COLUMN_MONSTER_TO_QUEST_UNSTABLE] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_UNSTABLE

        projectionMap[m + S.COLUMN_MONSTERS_NAME] = m + "." + S.COLUMN_MONSTERS_NAME + " AS " + m + S.COLUMN_MONSTERS_NAME
        projectionMap[S.COLUMN_MONSTERS_FILE_LOCATION] = m + "." + S.COLUMN_MONSTERS_FILE_LOCATION
        projectionMap[q + S.COLUMN_QUESTS_NAME] = q + "." + S.COLUMN_QUESTS_NAME + " AS " + q + S.COLUMN_QUESTS_NAME
        projectionMap[S.COLUMN_QUESTS_HUB] = q + "." + S.COLUMN_QUESTS_HUB
        projectionMap[S.COLUMN_QUESTS_STARS] = q + "." + S.COLUMN_QUESTS_STARS

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_MONSTER_TO_QUEST + " AS mtq" + " LEFT OUTER JOIN " + S.TABLE_MONSTERS + " AS m" + " ON " + "mtq." +
                S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID + " = " + "m." + S.COLUMN_MONSTERS_ID + " LEFT OUTER JOIN " + S.TABLE_QUESTS +
                " AS q " + " ON " + "mtq." + S.COLUMN_MONSTER_TO_QUEST_QUEST_ID + " = " + "q." + S.COLUMN_QUESTS_ID

        QB.setDistinct(Distinct)
        QB.setProjectionMap(projectionMap)
        return QB
    }

    /*
 * Helper method to query for MonsterToQuest
 */
    private fun builderMonsterToQuestWithHabitats(Distinct: Boolean): SQLiteQueryBuilder {
        //		SELECT mtq._id AS _id, mtq.monster_id, mtq.quest_id,
        //		mtq.unstable, m.name AS mname, q.name AS qname,
        //		q.hub, q.stars,mh.start_area,mh.move_area,mh.rest_area
        //		FROM monster_to_quest AS mtq
        //		LEFT OUTER JOIN monsters AS m ON mtq.monster_id = m._id
        //		LEFT OUTER JOIN quests AS q ON mtq.quest_id = q._id;
        //      LEFT OUTER JOIN monster_habitat mh ON mh.monster_id=m._id AND mh.location_id=q.location_id

        val mtq = "mtq"
        val m = "m"
        val q = "q"
        val mh = "mh"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_ID + " AS " + "_id"

        projectionMap[S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID
        projectionMap[S.COLUMN_MONSTER_TO_QUEST_QUEST_ID] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_QUEST_ID
        projectionMap[S.COLUMN_MONSTER_TO_QUEST_UNSTABLE] = mtq + "." + S.COLUMN_MONSTER_TO_QUEST_UNSTABLE

        projectionMap[m + S.COLUMN_MONSTERS_NAME] = m + "." + S.COLUMN_MONSTERS_NAME + " AS " + m + S.COLUMN_MONSTERS_NAME
        projectionMap[S.COLUMN_MONSTERS_FILE_LOCATION] = m + "." + S.COLUMN_MONSTERS_FILE_LOCATION
        projectionMap[q + S.COLUMN_QUESTS_NAME] = q + "." + S.COLUMN_QUESTS_NAME + " AS " + q + S.COLUMN_QUESTS_NAME
        projectionMap[S.COLUMN_QUESTS_HUB] = q + "." + S.COLUMN_QUESTS_HUB
        projectionMap[S.COLUMN_QUESTS_STARS] = q + "." + S.COLUMN_QUESTS_STARS

        projectionMap[S.COLUMN_HABITAT_START] = mh + "." + S.COLUMN_HABITAT_START
        projectionMap[S.COLUMN_HABITAT_AREAS] = mh + "." + S.COLUMN_HABITAT_AREAS
        projectionMap[S.COLUMN_HABITAT_REST] = mh + "." + S.COLUMN_HABITAT_REST


        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_MONSTER_TO_QUEST + " AS mtq" + " LEFT OUTER JOIN " + S.TABLE_MONSTERS + " AS m" + " ON " + "mtq." +
                S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID + " = " + "m." + S.COLUMN_MONSTERS_ID + " LEFT OUTER JOIN " + S.TABLE_QUESTS +
                " AS q " + " ON " + "mtq." + S.COLUMN_MONSTER_TO_QUEST_QUEST_ID + " = " + "q." + S.COLUMN_QUESTS_ID +
                " LEFT OUTER JOIN " + S.TABLE_HABITAT + " AS mh ON mh." + S.COLUMN_HABITAT_MONSTER_ID + "= m." + S.COLUMN_MONSTERS_ID +
                " AND mh." + S.COLUMN_HABITAT_LOCATION_ID + "= CASE WHEN q." + S.COLUMN_QUESTS_LOCATION_ID + ">=100 then q." + S.COLUMN_QUESTS_LOCATION_ID + "-100 ELSE q." + S.COLUMN_QUESTS_LOCATION_ID + " END"




        QB.setDistinct(Distinct)
        QB.setProjectionMap(projectionMap)
        return QB
    }


    /********************************* MONSTER WEAKNESS QUERIES  */
    /* Get all weaknesses a from a particular monster */
    fun queryWeaknessFromMonster(id: Long): MonsterWeaknessCursor {
        // SELECT * FROM monster_ailment WHERE monster_id = id

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_WEAKNESS
        qh.Columns = null
        qh.Selection = S.COLUMN_WEAKNESS_MONSTER_ID + " = " + id
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return MonsterWeaknessCursor(wrapHelper(qh))
    }

    /**
     * ****************************** QUEST QUERIES *****************************************
     */

    /*
	 * Get all quests
	 */
    fun queryQuests(): QuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUESTS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return QuestCursor(wrapJoinHelper(builderQuest(), qh))
    }

    /*
     * Get all quests by a filter
     */
    fun queryQuestsSearch(searchTerm: String): QuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUESTS
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        modifyQueryForSearch(qh, "q." + S.COLUMN_QUESTS_NAME, searchTerm)

        return QuestCursor(wrapJoinHelper(builderQuest(), qh))
    }

    /*
     * Get a specific quest
     */
    fun queryQuest(id: Long): QuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUESTS
        qh.Selection = "q." + S.COLUMN_QUESTS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return QuestCursor(wrapJoinHelper(builderQuest(), qh))
    }

    /*
     * Get a specific quest based on hub
     */
    fun queryQuestHub(hub: String): QuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUESTS
        qh.Selection = "q." + S.COLUMN_QUESTS_HUB + " = ? AND q." + S.COLUMN_QUESTS_NAME + " <> ''"
        qh.SelectionArgs = arrayOf(hub)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return QuestCursor(wrapJoinHelper(builderQuest(), qh))
    }

    /*
     * Get a specific quest based on hub and stars
     */
    fun queryQuestHubStar(hub: String, stars: String): QuestCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUESTS
        qh.Selection = "q." + S.COLUMN_QUESTS_HUB + " = ?" + " AND " +
                "q." + S.COLUMN_QUESTS_STARS + " = ?" + " AND " +
                "q." + S.COLUMN_QUESTS_NAME + " <> ''"
        qh.SelectionArgs = arrayOf(hub, stars)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return QuestCursor(wrapJoinHelper(builderQuest(), qh))
    }

    /*
     * Helper method to query for quests
     */
    private fun builderQuest(): SQLiteQueryBuilder {
        //		SELECT q._id AS _id, q.name AS qname, q.goal, q.hub, q.type, q.stars, q.location_id, q.time_limit,
        //		q.fee, q.reward, q.hrp,	l.name AS lname, l.map
        //		FROM quests AS q LEFT OUTER JOIN locations AS l ON q.location_id = l._id;

        val q = "q"
        val l = "l"

        val projectionMap = LinkedHashMap<String, String>()

        projectionMap["_id"] = q + "." + S.COLUMN_QUESTS_ID + " AS " + "_id"
        projectionMap[q + S.COLUMN_QUESTS_NAME] = q + "." + S.COLUMN_QUESTS_NAME + " AS " + q + S.COLUMN_QUESTS_NAME
        projectionMap[q + S.COLUMN_QUESTS_JPN_NAME] = q + "." + S.COLUMN_QUESTS_JPN_NAME + " AS " + q + S.COLUMN_QUESTS_JPN_NAME
        projectionMap[S.COLUMN_QUESTS_GOAL] = q + "." + S.COLUMN_QUESTS_GOAL
        projectionMap[S.COLUMN_QUESTS_HUB] = q + "." + S.COLUMN_QUESTS_HUB
        projectionMap[S.COLUMN_QUESTS_TYPE] = q + "." + S.COLUMN_QUESTS_TYPE
        projectionMap[S.COLUMN_QUESTS_STARS] = q + "." + S.COLUMN_QUESTS_STARS
        projectionMap[S.COLUMN_QUESTS_LOCATION_ID] = q + "." + S.COLUMN_QUESTS_LOCATION_ID
        //projectionMap.put(S.COLUMN_QUESTS_LOCATION_TIME, q + "." + S.COLUMN_QUESTS_LOCATION_TIME);
        projectionMap[S.COLUMN_QUESTS_TIME_LIMIT] = q + "." + S.COLUMN_QUESTS_TIME_LIMIT
        projectionMap[S.COLUMN_QUESTS_FEE] = q + "." + S.COLUMN_QUESTS_FEE
        projectionMap[S.COLUMN_QUESTS_REWARD] = q + "." + S.COLUMN_QUESTS_REWARD
        projectionMap[S.COLUMN_QUESTS_HRP] = q + "." + S.COLUMN_QUESTS_HRP
        projectionMap[S.COLUMN_QUESTS_SUB_GOAL] = q + "." + S.COLUMN_QUESTS_SUB_GOAL
        projectionMap[S.COLUMN_QUESTS_SUB_REWARD] = q + "." + S.COLUMN_QUESTS_SUB_REWARD
        projectionMap[S.COLUMN_QUESTS_SUB_HRP] = q + "." + S.COLUMN_QUESTS_SUB_HRP
        projectionMap[S.COLUMN_QUESTS_GOAL_TYPE] = q + "." + S.COLUMN_QUESTS_GOAL_TYPE
        projectionMap[S.COLUMN_QUESTS_HUNTER_TYPE] = q + "." + S.COLUMN_QUESTS_HUNTER_TYPE
        projectionMap[l + S.COLUMN_LOCATIONS_NAME] = l + "." + S.COLUMN_LOCATIONS_NAME + " AS " + l + S.COLUMN_LOCATIONS_NAME
        projectionMap[S.COLUMN_LOCATIONS_MAP] = l + "." + S.COLUMN_LOCATIONS_MAP
        projectionMap[S.COLUMN_QUESTS_FLAVOR] = q + "." + S.COLUMN_QUESTS_FLAVOR

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_QUESTS + " AS q" + " LEFT OUTER JOIN " + S.TABLE_LOCATIONS + " AS l" + " ON " + "q." +
                S.COLUMN_QUESTS_LOCATION_ID + " = " + "l." + S.COLUMN_LOCATIONS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** QUEST REWARD QUERIES *****************************************
     */

    /*
	 * Get all quest reward quests based on item
	 */
    fun queryQuestRewardItem(id: Long): QuestRewardCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUEST_REWARDS
        qh.Selection = "qr." + S.COLUMN_QUEST_REWARDS_ITEM_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = "q." + S.COLUMN_QUESTS_HUB + " ASC, " + "q." + S.COLUMN_QUESTS_STARS + " ASC"
        qh.Limit = null

        return QuestRewardCursor(wrapJoinHelper(builderQuestReward(), qh))
    }

    /*
     * Get all quest reward items based on quest
     */
    fun queryQuestRewardQuest(id: Long): QuestRewardCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_QUEST_REWARDS
        qh.Selection = "qr." + S.COLUMN_QUEST_REWARDS_QUEST_ID + " = ? "
        qh.SelectionArgs = arrayOf("" + id)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = S.COLUMN_QUEST_REWARDS_REWARD_SLOT
        qh.Limit = null

        return QuestRewardCursor(wrapJoinHelper(builderQuestReward(), qh))
    }

    /*
     * Helper method to query for QuestReward
     */
    private fun builderQuestReward(): SQLiteQueryBuilder {
        //		SELECT qr._id AS _id, qr.quest_id, qr.item_id,
        //		qr.reward_slot, qr.percentage, qr.stack_size,
        //		q.name AS qname, q.hub, q.stars, i.name AS iname
        //		FROM quest_rewards AS qr
        //		LEFT OUTER JOIN quests AS q ON qr.quest_id = q._id
        //		LEFT OUTER JOIN items AS i ON qr.item_id = i._id;

        val qr = "qr"
        val i = "i"
        val q = "q"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = qr + "." + S.COLUMN_QUEST_REWARDS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_QUEST_REWARDS_ITEM_ID] = qr + "." + S.COLUMN_QUEST_REWARDS_ITEM_ID
        projectionMap[S.COLUMN_QUEST_REWARDS_QUEST_ID] = qr + "." + S.COLUMN_QUEST_REWARDS_QUEST_ID
        projectionMap[S.COLUMN_QUEST_REWARDS_REWARD_SLOT] = qr + "." + S.COLUMN_QUEST_REWARDS_REWARD_SLOT
        projectionMap[S.COLUMN_QUEST_REWARDS_PERCENTAGE] = qr + "." + S.COLUMN_QUEST_REWARDS_PERCENTAGE
        projectionMap[S.COLUMN_QUEST_REWARDS_STACK_SIZE] = qr + "." + S.COLUMN_QUEST_REWARDS_STACK_SIZE

        projectionMap[i + S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME + " AS " + i + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[q + S.COLUMN_QUESTS_NAME] = q + "." + S.COLUMN_QUESTS_NAME + " AS " + q + S.COLUMN_QUESTS_NAME
        projectionMap[S.COLUMN_QUESTS_HUB] = q + "." + S.COLUMN_QUESTS_HUB
        projectionMap[S.COLUMN_QUESTS_STARS] = q + "." + S.COLUMN_QUESTS_STARS

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_QUEST_REWARDS + " AS qr" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "qr." +
                S.COLUMN_QUEST_REWARDS_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_QUESTS +
                " AS q " + " ON " + "qr." + S.COLUMN_QUEST_REWARDS_QUEST_ID + " = " + "q." + S.COLUMN_QUESTS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** SKILL QUERIES *****************************************
     */

    //	public SkillCursor querySkill(long id) {
    //		// "SELECT * FROM skills WHERE skill_id = id"
    //
    //		_Distinct = false;
    //		_Table = S.TABLE_SKILLS;
    //		_Columns = null;
    //		_Selection = S.COLUMN_SKILLS_ID + " = ?";
    //		_SelectionArgs = new String[]{ String.valueOf(id) };
    //		_GroupBy = null;
    //		_Having = null;
    //		_OrderBy = null;
    //		_Limit = null;
    //
    //		return new SkillCursor(wrapHelper());
    //	}

    /*
	 * Get all skills for a skill tree
	 */
    fun querySkillFromTree(id: Long): SkillCursor {
        // "SELECT * FROM skills WHERE skill_tree_id = id"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_SKILLS
        qh.Columns = null
        qh.Selection = S.COLUMN_SKILLS_SKILL_TREE_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return SkillCursor(wrapHelper(qh))
    }

    /**
     * ****************************** SKILL TREE QUERIES *****************************************
     */

    /*
	 * Get all skill tress
	 */
    fun querySkillTrees(): SkillTreeCursor {
        // "SELECT DISTINCT * FROM skill_trees GROUP BY name"

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_SKILL_TREES
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = S.COLUMN_SKILL_TREES_NAME
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return SkillTreeCursor(wrapHelper(qh))
    }

    /*
     * Get Skill trees filtered by name
     */
    fun querySkillTreesSearch(searchTerm: String): SkillTreeCursor {
        // "SELECT DISTINCT * FROM skill_trees
        //  WHERE (name LIKE '% word%' OR name LIKE 'word%')
        //    AND (name LIKE '% word2%' OR name LIKE 'word2%')
        //  GROUP BY name"

        val qh = QueryHelper()
        qh.Distinct = true
        qh.Table = S.TABLE_SKILL_TREES
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = S.COLUMN_SKILL_TREES_NAME
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        modifyQueryForSearch(qh, S.COLUMN_SKILL_TREES_NAME, searchTerm)

        return SkillTreeCursor(wrapHelper(qh))
    }

    /*
     * Get a specific skill tree
     */
    fun querySkillTree(id: Long): SkillTreeCursor {
        // "SELECT DISTINCT * FROM skill_trees WHERE _id = id LIMIT 1"

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_SKILL_TREES
        qh.Columns = null
        qh.Selection = S.COLUMN_SKILL_TREES_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return SkillTreeCursor(wrapHelper(qh))
    }

    /**
     * ****************************** WEAPON QUERIES *****************************************
     */

    /*
	 * Get all weapon
	 */
    fun queryWeapon(): WeaponCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_WEAPONS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WeaponCursor(wrapJoinHelper(builderWeapon(), qh))
    }

    /*
     * Get a specific weapon
     */
    fun queryWeapon(id: Long): WeaponCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_WEAPONS
        qh.Selection = "w." + S.COLUMN_WEAPONS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return WeaponCursor(wrapJoinHelper(builderWeapon(), qh))
    }

    /*
     * Get multiple specific weapon
     */
    fun queryWeapons(ids: LongArray): WeaponCursor {

        val string_list = arrayOfNulls<String>(ids.size)
        for (i in ids.indices) {
            string_list[i] = ids[i].toString()
        }

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_WEAPONS
        qh.Selection = "w." + S.COLUMN_WEAPONS_ID + " IN (" + makePlaceholders(ids.size) + ")"
        qh.SelectionArgs = string_list
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WeaponCursor(wrapJoinHelper(builderWeapon(), qh))
    }

    fun queryWeaponTypeForWeapon(id: Long): Cursor {
        val qh = QueryHelper()
        qh.Columns = arrayOf(S.COLUMN_WEAPONS_WTYPE)
        qh.Table = S.TABLE_WEAPONS
        qh.Selection = S.COLUMN_WEAPONS_ID + " = ? "
        qh.SelectionArgs = arrayOf(java.lang.Long.toString(id))
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null
        return writableDatabase.query(qh.Table, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)
    }

    /*
     * Get a specific weapon based on weapon type
     */
    fun queryWeaponType(type: String, finalOnly: Boolean): WeaponCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_WEAPONS
        qh.Selection = "w." + S.COLUMN_WEAPONS_WTYPE + " = ? "
        if (finalOnly) {
            qh.Selection += "AND w." + S.COLUMN_WEAPONS_FINAL + " = 1 "
        }
        qh.SelectionArgs = arrayOf(type)
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WeaponCursor(wrapJoinHelper(builderWeapon(), qh))
    }

    /*
     * Helper method to query for weapon
     */
    private fun builderWeapon(): SQLiteQueryBuilder {
        //		SELECT w._id AS _id, w.wtype, w.creation_cost, w.upgrade_cost, w.attack, w.max_attack,
        //		w.elemental_attack, w.awakened_elemental_attack, w.defense, w.sharpness, w.affinity,
        //		w.horn_notes, w.shelling_type, w.charge_levels, w.allowed_coatings, w.recoil, w.reload_speed,
        //		w.rapid_fire, w.normal_shots, w.status_shots, w.elemental_shots, w.tool_shots, w.num_slots,
        //		w.sharpness_file,
        //		i.name, i.jpn_name, i.type, i.rarity, i.carry_capacity, i.buy, i.sell, i.description,
        //		i.icon_name, i.armor_dupe_name_fix, w.special_ammo
        //		FROM weapons AS w LEFT OUTER JOIN	items AS i ON w._id = i._id;

        val w = "w"
        val i = "i"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = w + "." + S.COLUMN_WEAPONS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_WEAPONS_WTYPE] = w + "." + S.COLUMN_WEAPONS_WTYPE
        projectionMap[S.COLUMN_WEAPONS_CREATION_COST] = w + "." + S.COLUMN_WEAPONS_CREATION_COST
        projectionMap[S.COLUMN_WEAPONS_UPGRADE_COST] = w + "." + S.COLUMN_WEAPONS_UPGRADE_COST
        projectionMap[S.COLUMN_WEAPONS_ATTACK] = w + "." + S.COLUMN_WEAPONS_ATTACK
        projectionMap[S.COLUMN_WEAPONS_MAX_ATTACK] = w + "." + S.COLUMN_WEAPONS_MAX_ATTACK
        projectionMap[S.COLUMN_WEAPONS_ELEMENT] = w + "." + S.COLUMN_WEAPONS_ELEMENT
        projectionMap[S.COLUMN_WEAPONS_AWAKEN] = w + "." + S.COLUMN_WEAPONS_AWAKEN
        projectionMap[S.COLUMN_WEAPONS_ELEMENT_2] = w + "." + S.COLUMN_WEAPONS_ELEMENT_2
        projectionMap[S.COLUMN_WEAPONS_AWAKEN_ATTACK] = w + "." + S.COLUMN_WEAPONS_AWAKEN_ATTACK
        projectionMap[S.COLUMN_WEAPONS_ELEMENT_ATTACK] = w + "." + S.COLUMN_WEAPONS_ELEMENT_ATTACK
        projectionMap[S.COLUMN_WEAPONS_ELEMENT_2_ATTACK] = w + "." + S.COLUMN_WEAPONS_ELEMENT_2_ATTACK
        projectionMap[S.COLUMN_WEAPONS_DEFENSE] = w + "." + S.COLUMN_WEAPONS_DEFENSE
        projectionMap[S.COLUMN_WEAPONS_SHARPNESS] = w + "." + S.COLUMN_WEAPONS_SHARPNESS
        projectionMap[S.COLUMN_WEAPONS_AFFINITY] = w + "." + S.COLUMN_WEAPONS_AFFINITY
        projectionMap[S.COLUMN_WEAPONS_HORN_NOTES] = w + "." + S.COLUMN_WEAPONS_HORN_NOTES
        projectionMap[S.COLUMN_WEAPONS_SHELLING_TYPE] = w + "." + S.COLUMN_WEAPONS_SHELLING_TYPE
        projectionMap[S.COLUMN_WEAPONS_PHIAL] = w + "." + S.COLUMN_WEAPONS_PHIAL
        projectionMap[S.COLUMN_WEAPONS_CHARGES] = w + "." + S.COLUMN_WEAPONS_CHARGES
        projectionMap[S.COLUMN_WEAPONS_COATINGS] = w + "." + S.COLUMN_WEAPONS_COATINGS
        projectionMap[S.COLUMN_WEAPONS_RECOIL] = w + "." + S.COLUMN_WEAPONS_RECOIL
        projectionMap[S.COLUMN_WEAPONS_RELOAD_SPEED] = w + "." + S.COLUMN_WEAPONS_RELOAD_SPEED
        projectionMap[S.COLUMN_WEAPONS_RAPID_FIRE] = w + "." + S.COLUMN_WEAPONS_RAPID_FIRE
        projectionMap[S.COLUMN_WEAPONS_DEVIATION] = w + "." + S.COLUMN_WEAPONS_DEVIATION
        projectionMap[S.COLUMN_WEAPONS_AMMO] = w + "." + S.COLUMN_WEAPONS_AMMO
        projectionMap[S.COLUMN_WEAPONS_NUM_SLOTS] = w + "." + S.COLUMN_WEAPONS_NUM_SLOTS
        projectionMap[S.COLUMN_WEAPONS_FINAL] = w + "." + S.COLUMN_WEAPONS_FINAL
        projectionMap[S.COLUMN_WEAPONS_TREE_DEPTH] = w + "." + S.COLUMN_WEAPONS_TREE_DEPTH
        projectionMap[S.COLUMN_WEAPONS_PARENT_ID] = w + "." + S.COLUMN_WEAPONS_PARENT_ID
        projectionMap[S.COLUMN_WEAPONS_SPECIAL_AMMO] = w + "." + S.COLUMN_WEAPONS_SPECIAL_AMMO

        projectionMap[S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_JPN_NAME] = i + "." + S.COLUMN_ITEMS_JPN_NAME
        projectionMap[S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY
        projectionMap[S.COLUMN_ITEMS_CARRY_CAPACITY] = i + "." + S.COLUMN_ITEMS_CARRY_CAPACITY
        projectionMap[S.COLUMN_ITEMS_BUY] = i + "." + S.COLUMN_ITEMS_BUY
        projectionMap[S.COLUMN_ITEMS_SELL] = i + "." + S.COLUMN_ITEMS_SELL
        projectionMap[S.COLUMN_ITEMS_DESCRIPTION] = i + "." + S.COLUMN_ITEMS_DESCRIPTION
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX] = i + "." + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_WEAPONS + " AS w" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "w." +
                S.COLUMN_WEAPONS_ID + " = " + "i." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }


    /**
     * ****************************** WEAPON TREE QUERIES *****************************************
     */

    /*
	 * Get the parent weapon
	 */
    fun queryWeaponTreeParent(id: Long): WeaponTreeCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Selection = "i1." + S.COLUMN_ITEMS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WeaponTreeCursor(wrapJoinHelper(builderWeaponTreeParent(), qh))
    }

    /*
     * Get the child weapon
     */
    fun queryWeaponTreeChild(id: Long): WeaponTreeCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Selection = "i1." + S.COLUMN_ITEMS_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WeaponTreeCursor(wrapJoinHelper(builderWeaponTreeChild(), qh))
    }

    /*
     * Helper method to query for weapon tree parent
     */
    private fun builderWeaponTreeParent(): SQLiteQueryBuilder {
        //		SELECT i2._id, i2.name
        //		FROM items AS i1
        //		LEFT OUTER JOIN components AS c ON i1._id = c.created_item_id
        //		JOIN weapons AS w2 ON w2._id = c.component_item_id
        //		LEFT OUTER JOIN items AS i2 ON i2._id = w2._id
        //
        //		WHERE i1._id = 'id';

        val i1 = "i1"
        val i2 = "i2"
        val w2 = "w2"
        val c = "c"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = i2 + "." + S.COLUMN_ITEMS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_ITEMS_NAME] = i2 + "." + S.COLUMN_ITEMS_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ITEMS + " AS i1" + " LEFT OUTER JOIN " + S.TABLE_COMPONENTS + " AS c" +
                " ON " + "i1." + S.COLUMN_ITEMS_ID + " = " + "c." + S.COLUMN_COMPONENTS_CREATED_ITEM_ID +
                " JOIN " + S.TABLE_WEAPONS + " AS w2" + " ON " + "w2." + S.COLUMN_WEAPONS_ID + " = " +
                "c." + S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID + " LEFT OUTER JOIN " + S.TABLE_ITEMS +
                " AS i2" + " ON " + "i2." + S.COLUMN_ITEMS_ID + " = " + "w2." + S.COLUMN_WEAPONS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /*
     * Helper method to query for weapon tree child
     */
    private fun builderWeaponTreeChild(): SQLiteQueryBuilder {
        //		SELECT i2._id, i2.name
        //		FROM items AS i1
        //		LEFT OUTER JOIN components AS c ON i1._id = c.component_item_id
        //		JOIN weapons AS w2 ON w2._id = c.created_item_id
        //		LEFT OUTER JOIN items AS i2 ON i2._id = w2._id
        //
        //		WHERE i1._id = '_id';

        val i1 = "i1"
        val i2 = "i2"
        val w2 = "w2"
        val c = "c"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = i2 + "." + S.COLUMN_ITEMS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_ITEMS_NAME] = i2 + "." + S.COLUMN_ITEMS_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_ITEMS + " AS i1" + " LEFT OUTER JOIN " + S.TABLE_COMPONENTS + " AS c" +
                " ON " + "i1." + S.COLUMN_ITEMS_ID + " = " + "c." + S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID +
                " JOIN " + S.TABLE_WEAPONS + " AS w2" + " ON " + "w2." + S.COLUMN_WEAPONS_ID + " = " +
                "c." + S.COLUMN_COMPONENTS_CREATED_ITEM_ID + " LEFT OUTER JOIN " + S.TABLE_ITEMS +
                " AS i2" + " ON " + "i2." + S.COLUMN_ITEMS_ID + " = " + "w2." + S.COLUMN_WEAPONS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** PALICO WEAPON QUERIES *****************************************
     */

    fun queryPalicoWeapons(): PalicoWeaponCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_PALICO_WEAPONS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = S.COLUMN_ITEMS_RARITY
        qh.Limit = null

        return PalicoWeaponCursor(wrapJoinHelper(builderPalicoWeapon(), qh))
    }

    fun queryPalicoWeapon(id: Long): PalicoWeaponCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_PALICO_WEAPONS
        qh.Selection = "w." + S.COLUMN_PALICO_WEAPONS_ID + "=?"
        qh.SelectionArgs = arrayOf(java.lang.Long.toString(id))
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = S.COLUMN_ITEMS_RARITY
        qh.Limit = null
        return PalicoWeaponCursor(wrapJoinHelper(builderPalicoWeapon(), qh))
    }

    private fun builderPalicoWeapon(): SQLiteQueryBuilder {
        val w = "w"
        val i = "i"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = w + "." + S.COLUMN_PALICO_WEAPONS_ID + " AS " + "_id"
        projectionMap[S.COLUMN_PALICO_WEAPONS_CREATION_COST] = w + "." + S.COLUMN_PALICO_WEAPONS_CREATION_COST
        projectionMap[S.COLUMN_PALICO_WEAPONS_ATTACK_MELEE] = w + "." + S.COLUMN_PALICO_WEAPONS_ATTACK_MELEE
        projectionMap[S.COLUMN_PALICO_WEAPONS_ATTACK_RANGED] = w + "." + S.COLUMN_PALICO_WEAPONS_ATTACK_RANGED
        projectionMap[S.COLUMN_PALICO_WEAPONS_ELEMENT] = w + "." + S.COLUMN_PALICO_WEAPONS_ELEMENT
        projectionMap[S.COLUMN_PALICO_WEAPONS_ELEMENT_MELEE] = w + "." + S.COLUMN_PALICO_WEAPONS_ELEMENT_MELEE
        projectionMap[S.COLUMN_PALICO_WEAPONS_ELEMENT_RANGED] = w + "." + S.COLUMN_PALICO_WEAPONS_ELEMENT_RANGED
        projectionMap[S.COLUMN_PALICO_WEAPONS_BLUNT] = w + "." + S.COLUMN_PALICO_WEAPONS_BLUNT
        projectionMap[S.COLUMN_PALICO_WEAPONS_BALANCE] = w + "." + S.COLUMN_PALICO_WEAPONS_BALANCE
        projectionMap[S.COLUMN_PALICO_WEAPONS_DEFENSE] = w + "." + S.COLUMN_PALICO_WEAPONS_DEFENSE
        projectionMap[S.COLUMN_PALICO_WEAPONS_SHARPNESS] = w + "." + S.COLUMN_PALICO_WEAPONS_SHARPNESS
        projectionMap[S.COLUMN_PALICO_WEAPONS_AFFINITY_MELEE] = w + "." + S.COLUMN_PALICO_WEAPONS_AFFINITY_MELEE
        projectionMap[S.COLUMN_PALICO_WEAPONS_AFFINITY_RANGED] = w + "." + S.COLUMN_PALICO_WEAPONS_AFFINITY_RANGED

        projectionMap[S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY
        projectionMap[S.COLUMN_ITEMS_DESCRIPTION] = i + "." + S.COLUMN_ITEMS_DESCRIPTION
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_PALICO_WEAPONS + " AS w" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i" + " ON " + "w." +
                S.COLUMN_PALICO_WEAPONS_ID + " = " + "i." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** WISHLIST QUERIES *****************************************
     */

    /*
	 * Get all wishlist
	 */
    fun queryWishlists(): WishlistCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WishlistCursor(wrapHelper(qh))
    }

    /*
     * Get all wishlist using a specific db instance
     */
    fun queryWishlists(db: SQLiteDatabase): WishlistCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WishlistCursor(wrapHelper(db, qh))
    }

    /*
     * Get a specific wishlist
     */
    fun queryWishlist(id: Long): WishlistCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST
        qh.Columns = null
        qh.Selection = S.COLUMN_WISHLIST_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return WishlistCursor(wrapHelper(qh))
    }

    /*
     * Add a wishlist
     */
    fun queryAddWishlist(name: String): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_NAME, name)

        return insertRecord(S.TABLE_WISHLIST, values)
    }

    /*
     * Add a wishlist with all info
     */
    fun queryAddWishlistAll(db: SQLiteDatabase, id: Long, name: String): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_ID, id)
        values.put(S.COLUMN_WISHLIST_NAME, name)

        return insertRecord(db, S.TABLE_WISHLIST, values)
    }

    fun queryUpdateWishlist(id: Long, name: String): Int {
        val strFilter = S.COLUMN_WISHLIST_ID + " = " + id

        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_NAME, name)

        return updateRecord(S.TABLE_WISHLIST, strFilter, values)
    }

    fun queryDeleteWishlist(id: Long): Boolean {
        var where = S.COLUMN_WISHLIST_ID + " = ?"
        val args = arrayOf("" + id)
        val w1 = deleteRecord(S.TABLE_WISHLIST, where, args)

        where = S.COLUMN_WISHLIST_DATA_WISHLIST_ID + " = ?"
        val w2 = deleteRecord(S.TABLE_WISHLIST_DATA, where, args)

        return w1 && w2
    }

    /**
     * ****************************** WISHLIST DATA QUERIES *****************************************
     */

    /*
	 * Get all wishlist data
	 */
    fun queryWishlistsData(): WishlistDataCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST_DATA
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        // Multithread issues workaround
        val qb = builderWishlistData()
        val cursor = qb.query(
                writableDatabase, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)

        return WishlistDataCursor(cursor)
    }

    /*
     * Get all wishlist data using specific db instance
     */
    fun queryWishlistsData(db: SQLiteDatabase): WishlistDataCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST_DATA
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        // Multithread issues workaround
        val qb = builderWishlistData()
        val cursor = qb.query(
                db, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)

        return WishlistDataCursor(cursor)
    }

    /*
     * Get all wishlist data for a specific wishlist
     */
    fun queryWishlistData(id: Long): WishlistDataCursor {

        val wdColumns: Array<String>? = null
        val wdSelection = "wd." + S.COLUMN_WISHLIST_DATA_WISHLIST_ID + " = ?"
        val wdSelectionArgs = arrayOf(id.toString())
        val wdGroupBy: String? = null
        val wdHaving: String? = null
        val wdOrderBy = "wd." + S.COLUMN_WISHLIST_DATA_ITEM_ID + " ASC"
        val wdLimit: String? = null

        // Multithread issues workaround
        val qb = builderWishlistData()
        val cursor = qb.query(
                writableDatabase, wdColumns, wdSelection, wdSelectionArgs, wdGroupBy, wdHaving, wdOrderBy, wdLimit)

        return WishlistDataCursor(cursor)
    }


    /*
     * Get all wishlist data for a specific wishlist data id
     */
    fun queryWishlistDataId(id: Long): WishlistDataCursor {

        val wdColumns: Array<String>? = null
        val wdSelection = "wd." + S.COLUMN_WISHLIST_DATA_ID + " = ?"
        val wdSelectionArgs = arrayOf(id.toString())
        val wdGroupBy: String? = null
        val wdHaving: String? = null
        val wdOrderBy: String? = null
        val wdLimit: String? = null

        // Multithread issues workaround
        val qb = builderWishlistData()
        val cursor = qb.query(
                writableDatabase, wdColumns, wdSelection, wdSelectionArgs, wdGroupBy, wdHaving, wdOrderBy, wdLimit)

        return WishlistDataCursor(cursor)
    }

    /*
     * Get all data for a specific wishlist and item
     */
    fun queryWishlistData(wd_id: Long, item_id: Long, path: String): WishlistDataCursor {

        val wdColumns: Array<String>? = null
        val wdSelection = "wd." + S.COLUMN_WISHLIST_DATA_WISHLIST_ID + " = ?" +
                " AND " + "wd." + S.COLUMN_WISHLIST_DATA_ITEM_ID + " = ?" +
                " AND " + "wd." + S.COLUMN_WISHLIST_DATA_PATH + " = ?"
        val wdSelectionArgs = arrayOf(wd_id.toString(), item_id.toString(), path)
        val wdGroupBy: String? = null
        val wdHaving: String? = null
        val wdOrderBy: String? = null
        val wdLimit: String? = null

        // Multithread issues workaround
        val qb = builderWishlistData()
        val cursor = qb.query(
                writableDatabase, wdColumns, wdSelection, wdSelectionArgs, wdGroupBy, wdHaving, wdOrderBy, wdLimit)

        return WishlistDataCursor(cursor)
    }

    /*
     * Add a wishlist data to a specific wishlist
     */
    fun queryAddWishlistData(wishlist_id: Long, item_id: Long,
                             quantity: Int, path: String): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_DATA_WISHLIST_ID, wishlist_id)
        values.put(S.COLUMN_WISHLIST_DATA_ITEM_ID, item_id)
        values.put(S.COLUMN_WISHLIST_DATA_QUANTITY, quantity)
        values.put(S.COLUMN_WISHLIST_DATA_PATH, path)

        return insertRecord(S.TABLE_WISHLIST_DATA, values)
    }

    /*
     * Add a wishlist data to a specific wishlist for copying
     */
    fun queryAddWishlistDataAll(wishlist_id: Long, item_id: Long,
                                quantity: Int, satisfied: Int, path: String): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_DATA_WISHLIST_ID, wishlist_id)
        values.put(S.COLUMN_WISHLIST_DATA_ITEM_ID, item_id)
        values.put(S.COLUMN_WISHLIST_DATA_QUANTITY, quantity)
        values.put(S.COLUMN_WISHLIST_DATA_SATISFIED, satisfied)
        values.put(S.COLUMN_WISHLIST_DATA_PATH, path)

        return insertRecord(S.TABLE_WISHLIST_DATA, values)
    }

    /*
     * Add a wishlist data to a specific wishlist for copying
     */
    fun queryAddWishlistDataAll(db: SQLiteDatabase, wishlist_id: Long, item_id: Long,
                                quantity: Int, satisfied: Int, path: String): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_DATA_WISHLIST_ID, wishlist_id)
        values.put(S.COLUMN_WISHLIST_DATA_ITEM_ID, item_id)
        values.put(S.COLUMN_WISHLIST_DATA_QUANTITY, quantity)
        values.put(S.COLUMN_WISHLIST_DATA_SATISFIED, satisfied)
        values.put(S.COLUMN_WISHLIST_DATA_PATH, path)

        return insertRecord(db, S.TABLE_WISHLIST_DATA, values)
    }

    /*
     * Update a wishlist data to a specific wishlist
     */
    fun queryUpdateWishlistDataQuantity(id: Long, quantity: Int): Int {
        val strFilter = S.COLUMN_WISHLIST_DATA_ID + " = " + id

        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_DATA_QUANTITY, quantity)

        return updateRecord(S.TABLE_WISHLIST_DATA, strFilter, values)
    }

    /*
     * Update a wishlist data to a specific wishlist
     */
    fun queryUpdateWishlistDataSatisfied(id: Long, satisfied: Int): Int {
        val strFilter = S.COLUMN_WISHLIST_DATA_ID + " = " + id

        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_DATA_SATISFIED, satisfied)

        return updateRecord(S.TABLE_WISHLIST_DATA, strFilter, values)
    }

    fun queryDeleteWishlistData(id: Long): Boolean {
        val where = S.COLUMN_WISHLIST_DATA_ID + " = ?"
        val args = arrayOf("" + id)
        return deleteRecord(S.TABLE_WISHLIST_DATA, where, args)
    }

    /*
     * Helper method to query for wishlistData
     */
    private fun builderWishlistData(): SQLiteQueryBuilder {
        //		SELECT wd._id AS _id, wd.wishlist_id, wd.item_id, wd.quantity, wd.satisfied, wd.path
        //		i.name, i.jpn_name, i.type, i.rarity, i.carry_capacity, i.buy, i.sell, i.description,
        //		i.icon_name, i.armor_dupe_name_fix
        //		FROM wishlist_data AS wd
        //		LEFT OUTER JOIN wishlist AS w ON wd.wishlist_id = w._id
        //		LEFT OUTER JOIN	items AS i ON wd.item_id = i._id;

        val wd = "wd"
        val w = "w"
        val i = "i"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = wd + "." + S.COLUMN_WISHLIST_DATA_ID + " AS " + "_id"
        projectionMap[S.COLUMN_WISHLIST_DATA_WISHLIST_ID] = wd + "." + S.COLUMN_WISHLIST_DATA_WISHLIST_ID
        projectionMap[S.COLUMN_WISHLIST_DATA_ITEM_ID] = wd + "." + S.COLUMN_WISHLIST_DATA_ITEM_ID
        projectionMap[S.COLUMN_WISHLIST_DATA_QUANTITY] = wd + "." + S.COLUMN_WISHLIST_DATA_QUANTITY
        projectionMap[S.COLUMN_WISHLIST_DATA_SATISFIED] = wd + "." + S.COLUMN_WISHLIST_DATA_SATISFIED
        projectionMap[S.COLUMN_WISHLIST_DATA_PATH] = wd + "." + S.COLUMN_WISHLIST_DATA_PATH

        projectionMap[S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME
        //projectionMap.put(S.COLUMN_ITEMS_JPN_NAME, i + "." + S.COLUMN_ITEMS_JPN_NAME);
        projectionMap[S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY
        projectionMap[S.COLUMN_ITEMS_CARRY_CAPACITY] = i + "." + S.COLUMN_ITEMS_CARRY_CAPACITY
        projectionMap[S.COLUMN_ITEMS_BUY] = i + "." + S.COLUMN_ITEMS_BUY
        projectionMap[S.COLUMN_ITEMS_SELL] = i + "." + S.COLUMN_ITEMS_SELL
        projectionMap[S.COLUMN_ITEMS_DESCRIPTION] = i + "." + S.COLUMN_ITEMS_DESCRIPTION
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX] = i + "." + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_WISHLIST_DATA + " AS wd" + " LEFT OUTER JOIN " + S.TABLE_WISHLIST + " AS w" + " ON " +
                "wd." + S.COLUMN_WISHLIST_DATA_WISHLIST_ID + " = " + "w." + S.COLUMN_WISHLIST_ID + " LEFT OUTER JOIN " +
                S.TABLE_ITEMS + " AS i" + " ON " + "wd." + S.COLUMN_WISHLIST_DATA_ITEM_ID + " = " + "i." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * ****************************** WISHLIST COMPONENT QUERIES *****************************************
     */

    /*
	 * Get all wishlist components
	 */
    fun queryWishlistsComponent(): WishlistComponentCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST_COMPONENT
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        // Multithread issues workaround
        val qb = builderWishlistComponent()
        val cursor = qb.query(
                writableDatabase, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)

        return WishlistComponentCursor(cursor)
    }

    /**
     * Get all wishlist components using a specific db instance
     *
     * @param db
     * @return
     */
    fun queryWishlistsComponent(db: SQLiteDatabase): WishlistComponentCursor {

        val qh = QueryHelper()
        qh.Distinct = false
        qh.Table = S.TABLE_WISHLIST_COMPONENT
        qh.Columns = null
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        // Multithread issues workaround
        val qb = builderWishlistComponent()
        val cursor = qb.query(
                db, qh.Columns, qh.Selection, qh.SelectionArgs, qh.GroupBy, qh.Having, qh.OrderBy, qh.Limit)

        return WishlistComponentCursor(cursor)
    }

    /*
     * Get all wishlist components for a specific wishlist
     */
    fun queryWishlistComponents(id: Long): WishlistComponentCursor {

        val wcColumns: Array<String>? = null
        val wcSelection = "wc." + S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID + " = ?"
        val wcSelectionArgs = arrayOf(id.toString())
        val wcGroupBy: String? = null
        val wcHaving: String? = null
        val wcOrderBy = "wc." + S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID + " ASC"
        val wcLimit: String? = null

        // Multithread issues workaround
        val qb = builderWishlistComponent()
        val cursor = qb.query(
                writableDatabase, wcColumns, wcSelection, wcSelectionArgs, wcGroupBy, wcHaving, wcOrderBy, wcLimit)

        return WishlistComponentCursor(cursor)
    }

    /*
     * Get all data for a specific wishlist and item
     */
    fun queryWishlistComponent(wc_id: Long, item_id: Long): WishlistComponentCursor {

        val wcColumns: Array<String>? = null
        val wcSelection = "wc." + S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID + " = ?" + " AND " +
                "wc." + S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID + " = ?"
        val wcSelectionArgs = arrayOf(wc_id.toString(), item_id.toString())
        val wcGroupBy: String? = null
        val wcHaving: String? = null
        val wcOrderBy: String? = null
        val wcLimit: String? = null

        // Multithread issues workaround
        val qb = builderWishlistComponent()
        val cursor = qb.query(
                writableDatabase, wcColumns, wcSelection, wcSelectionArgs, wcGroupBy, wcHaving, wcOrderBy, wcLimit)

        return WishlistComponentCursor(cursor)
    }

    /*
     * Get all wishlist components for a specific id
     */
    fun queryWishlistComponentId(id: Long): WishlistComponentCursor {

        val wcColumns: Array<String>? = null
        val wcSelection = "wc." + S.COLUMN_WISHLIST_COMPONENT_ID + " = ?"
        val wcSelectionArgs = arrayOf(id.toString())
        val wcGroupBy: String? = null
        val wcHaving: String? = null
        val wcOrderBy: String? = null
        val wcLimit: String? = null

        // Multithread issues workaround
        val qb = builderWishlistComponent()
        val cursor = qb.query(
                writableDatabase, wcColumns, wcSelection, wcSelectionArgs, wcGroupBy, wcHaving, wcOrderBy, wcLimit)

        return WishlistComponentCursor(cursor)
    }

    /*
     * Add a wishlist component to a specific wishlist
     */
    fun queryAddWishlistComponent(wishlist_id: Long, component_id: Long, quantity: Int): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID, wishlist_id)
        values.put(S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID, component_id)
        values.put(S.COLUMN_WISHLIST_COMPONENT_QUANTITY, quantity)

        return insertRecord(S.TABLE_WISHLIST_COMPONENT, values)
    }

    /*
     * Add a wishlist component to a specific wishlist
     */
    fun queryAddWishlistComponentAll(wishlist_id: Long, component_id: Long, quantity: Int, notes: Int): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID, wishlist_id)
        values.put(S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID, component_id)
        values.put(S.COLUMN_WISHLIST_COMPONENT_QUANTITY, quantity)
        values.put(S.COLUMN_WISHLIST_COMPONENT_NOTES, notes)

        return insertRecord(S.TABLE_WISHLIST_COMPONENT, values)
    }

    /*
     * Add a wishlist component to a specific wishlist
     */
    fun queryAddWishlistComponentAll(db: SQLiteDatabase, wishlist_id: Long, component_id: Long, quantity: Int, notes: Int): Long {
        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID, wishlist_id)
        values.put(S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID, component_id)
        values.put(S.COLUMN_WISHLIST_COMPONENT_QUANTITY, quantity)
        values.put(S.COLUMN_WISHLIST_COMPONENT_NOTES, notes)

        return insertRecord(db, S.TABLE_WISHLIST_COMPONENT, values)
    }

    /*
     * Update a wishlist component to a specific wishlist
     */
    fun queryUpdateWishlistComponentQuantity(id: Long, quantity: Int): Int {
        val strFilter = S.COLUMN_WISHLIST_COMPONENT_ID + " = " + id

        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_COMPONENT_QUANTITY, quantity)

        return updateRecord(S.TABLE_WISHLIST_COMPONENT, strFilter, values)
    }

    fun queryDeleteWishlistComponent(id: Long): Boolean {
        val where = S.COLUMN_WISHLIST_COMPONENT_ID + " = ?"
        val args = arrayOf("" + id)
        return deleteRecord(S.TABLE_WISHLIST_COMPONENT, where, args)
    }

    /*
     * Update a wishlist component to a specific wishlist
     */
    fun queryUpdateWishlistComponentNotes(id: Long, notes: Int): Int {
        val strFilter = S.COLUMN_WISHLIST_COMPONENT_ID + " = " + id

        val values = ContentValues()
        values.put(S.COLUMN_WISHLIST_COMPONENT_NOTES, notes)

        return updateRecord(S.TABLE_WISHLIST_COMPONENT, strFilter, values)
    }

    /*
     * Helper method to query components for wishlistData
     */
    private fun builderWishlistComponent(): SQLiteQueryBuilder {

        //		SELECT wc._id AS _id, wc.wishlist_id, wc.component_id, wc.quantity, wc.notes
        //		i.name, i.jpn_name, i.type, i.sub_type, i.rarity, i.carry_capacity, i.buy, i.sell, i.description,
        //		i.icon_name, i.armor_dupe_name_fix
        //		FROM wishlist_component AS wc
        //		LEFT OUTER JOIN wishlist AS w ON wd.wishlist_id = w._ic
        //		LEFT OUTER JOIN	items AS i ON wc.component_id = i._id;

        val wc = "wc"
        val w = "w"
        val i = "i"

        val projectionMap = HashMap<String, String>()

        projectionMap["_id"] = wc + "." + S.COLUMN_WISHLIST_COMPONENT_ID + " AS " + "_id"
        projectionMap[S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID] = wc + "." + S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID
        projectionMap[S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID] = wc + "." + S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID
        projectionMap[S.COLUMN_WISHLIST_COMPONENT_QUANTITY] = wc + "." + S.COLUMN_WISHLIST_COMPONENT_QUANTITY
        projectionMap[S.COLUMN_WISHLIST_COMPONENT_NOTES] = wc + "." + S.COLUMN_WISHLIST_COMPONENT_NOTES

        projectionMap[S.COLUMN_ITEMS_NAME] = i + "." + S.COLUMN_ITEMS_NAME
        //projectionMap.put(S.COLUMN_ITEMS_JPN_NAME, i + "." + S.COLUMN_ITEMS_JPN_NAME);
        projectionMap[S.COLUMN_ITEMS_TYPE] = i + "." + S.COLUMN_ITEMS_TYPE
        projectionMap[S.COLUMN_ITEMS_SUB_TYPE] = i + "." + S.COLUMN_ITEMS_SUB_TYPE
        projectionMap[S.COLUMN_ITEMS_RARITY] = i + "." + S.COLUMN_ITEMS_RARITY
        projectionMap[S.COLUMN_ITEMS_CARRY_CAPACITY] = i + "." + S.COLUMN_ITEMS_CARRY_CAPACITY
        projectionMap[S.COLUMN_ITEMS_BUY] = i + "." + S.COLUMN_ITEMS_BUY
        projectionMap[S.COLUMN_ITEMS_SELL] = i + "." + S.COLUMN_ITEMS_SELL
        projectionMap[S.COLUMN_ITEMS_DESCRIPTION] = i + "." + S.COLUMN_ITEMS_DESCRIPTION
        projectionMap[S.COLUMN_ITEMS_ICON_NAME] = i + "." + S.COLUMN_ITEMS_ICON_NAME
        projectionMap[S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX] = i + "." + S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_WISHLIST_COMPONENT + " AS wc" + " LEFT OUTER JOIN " + S.TABLE_WISHLIST + " AS w" + " ON " +
                "wc." + S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID + " = " + "w." + S.COLUMN_WISHLIST_ID + " LEFT OUTER JOIN " +
                S.TABLE_ITEMS + " AS i" + " ON " + "wc." + S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID + " = " +
                "i." + S.COLUMN_ITEMS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /**
     * *************************** WYPORIUM TRADE QUERIES ***************************************
     */

    /*
	 * Get all trades
	 */
    fun queryWyporiumTrades(): WyporiumTradeCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_WYPORIUM_TRADE
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return WyporiumTradeCursor(wrapJoinHelper(builderWyporiumTrade(), qh))
    }

    /*
	 * Get a specific wyporium trade
	 */
    fun queryWyporiumTrades(id: Long): WyporiumTradeCursor {

        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_WYPORIUM_TRADE
        qh.Selection = "wt.item_in_id = ? OR wt.item_out_id = ?"
        qh.SelectionArgs = arrayOf(id.toString(), id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return WyporiumTradeCursor(wrapJoinHelper(builderWyporiumTrade(), qh))
    }

    /*
     * Helper method to query for wyporium trades
     */
    private fun builderWyporiumTrade(): SQLiteQueryBuilder {
        //      SELECT wt._id AS trade_id, wt.item_in_id AS in_id, wt.item_out_id AS out_id, wt.unlock_quest_id AS q_id,
        //      i1.name AS in_name, i1.icon_name AS in_icon_name, i2.name AS out_name, i2.icon_name AS out_icon_name,
        //      q.name AS q_name
        //      FROM wyporium AS wt LEFT OUTER JOIN items AS i1 ON wt.item_in_id = i1._id
        //      LEFT OUTER JOIN items AS i2 ON wt.item_out_id = i2._id
        //      LEFT OUTER JOIN quests AS q ON wt.unlock_quest_id = q._id;

        val projectionMap = HashMap<String, String>()
        projectionMap["trade_id"] = "wt." + S.COLUMN_WYPORIUM_TRADE_ID + " AS " + "trade_id"
        projectionMap["in_id"] = "wt." + S.COLUMN_WYPORIUM_TRADE_ITEM_IN_ID + " AS " + "in_id"
        projectionMap["out_id"] = "wt." + S.COLUMN_WYPORIUM_TRADE_ITEM_OUT_ID + " AS " + "out_id"
        projectionMap["q_id"] = "wt." + S.COLUMN_WYPORIUM_TRADE_UNLOCK_QUEST_ID + " AS " + "q_id"
        projectionMap[S.COLUMN_ITEMS_ID] = "i1." + S.COLUMN_ITEMS_ID
        projectionMap["in_name"] = "i1." + S.COLUMN_ITEMS_NAME + " AS " + "in_name"
        projectionMap["in_icon_name"] = "i1." + S.COLUMN_ITEMS_ICON_NAME + " AS " + "in_icon_name"
        projectionMap[S.COLUMN_ITEMS_ID] = "i2." + S.COLUMN_ITEMS_ID
        projectionMap["out_name"] = "i2." + S.COLUMN_ITEMS_NAME + " AS " + "out_name"
        projectionMap["out_icon_name"] = "i2." + S.COLUMN_ITEMS_ICON_NAME + " AS " + "out_icon_name"
        projectionMap[S.COLUMN_QUESTS_ID] = "q." + S.COLUMN_QUESTS_ID
        projectionMap["q_name"] = "q." + S.COLUMN_QUESTS_NAME + " AS " + "q_name"

        //Create new querybuilder
        val QB = SQLiteQueryBuilder()

        QB.tables = S.TABLE_WYPORIUM_TRADE + " AS wt" + " LEFT OUTER JOIN " + S.TABLE_ITEMS + " AS i1" + " ON " + "wt." +
                S.COLUMN_WYPORIUM_TRADE_ITEM_IN_ID + " = " + "i1." + S.COLUMN_ITEMS_ID + " LEFT OUTER JOIN " + S.TABLE_ITEMS +
                " AS i2 " + " ON " + "wt." + S.COLUMN_WYPORIUM_TRADE_ITEM_OUT_ID + " = " + "i2." + S.COLUMN_ITEMS_ID +
                " LEFT OUTER JOIN " + S.TABLE_QUESTS + " AS q " + " ON " + "wt." + S.COLUMN_WYPORIUM_TRADE_UNLOCK_QUEST_ID + " = " +
                "q." + S.COLUMN_QUESTS_ID

        QB.setProjectionMap(projectionMap)
        return QB
    }

    /********************************* ARMOR SET BUILDER QUERIES  */

    /**
     * Get all armor sets.
     */
    fun queryASBSets(): ASBSetCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ASB_SETS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ASBSetCursor(wrapJoinHelper(builderASBSet(), qh))
    }

    /**
     * Retrieves a specific Armor Set Builder set in the database.
     */
    fun queryASBSet(id: Long): ASBSetCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ASB_SETS
        qh.Selection = "ar." + S.COLUMN_ASB_SET_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return ASBSetCursor(wrapJoinHelper(builderASBSet(), qh))
    }

    /**
     * Get all armor sets.
     */
    fun queryASBSessions(): ASBSessionCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ASB_SETS
        qh.Selection = null
        qh.SelectionArgs = null
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = null

        return ASBSessionCursor(wrapJoinHelper(builderASBSession(), qh))
    }

    /**
     * Get all armor sets.
     */
    fun queryASBSessions(db: SQLiteDatabase): Cursor {
        return db.rawQuery("SELECT * FROM " + S.TABLE_ASB_SETS, null)
    }

    /**
     * Retrieves a specific Armor Set Builder set in the database.
     */
    fun queryASBSession(id: Long): ASBSessionCursor {
        val qh = QueryHelper()
        qh.Columns = null
        qh.Table = S.TABLE_ASB_SETS
        qh.Selection = "ar." + S.COLUMN_ASB_SET_ID + " = ?"
        qh.SelectionArgs = arrayOf(id.toString())
        qh.GroupBy = null
        qh.Having = null
        qh.OrderBy = null
        qh.Limit = "1"

        return ASBSessionCursor(wrapJoinHelper(builderASBSession(), qh))
    }

    /**
     * Creates a new Armor Set Builder set in the entries of the database.
     */
    fun queryAddASBSet(name: String, rank: Int, hunterType: Int): Long {
        val values = ContentValues()

        values.put(S.COLUMN_ASB_SET_NAME, name)
        values.put(S.COLUMN_ASB_SET_RANK, rank)
        values.put(S.COLUMN_ASB_SET_HUNTER_TYPE, hunterType)
        values.put(S.COLUMN_TALISMAN_EXISTS, 0)

        return insertRecord(S.TABLE_ASB_SETS, values)
    }

    fun queryUpdateASBSet(asbSetId: Long, name: String, rank: Int, hunterType: Int): Long {
        val filter = S.COLUMN_ASB_SET_ID + " = " + asbSetId

        val values = ContentValues()

        values.put(S.COLUMN_ASB_SET_NAME, name)
        values.put(S.COLUMN_ASB_SET_RANK, rank)
        values.put(S.COLUMN_ASB_SET_HUNTER_TYPE, hunterType)
        values.put(S.COLUMN_TALISMAN_EXISTS, 0)

        return updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }

    fun queryDeleteASBSet(setId: Long): Boolean {
        val filter = S.COLUMN_ASB_SET_ID + " = " + setId

        return deleteRecord(S.TABLE_ASB_SETS, filter, emptyArray())
    }

    fun queryAddASBSessionArmor(asbSetId: Long, pieceId: Long, pieceIndex: Int): Long {
        val filter = S.COLUMN_ASB_SET_ID + " = " + asbSetId

        val values = ContentValues()

        when (pieceIndex) {
            ASBSession.HEAD -> putASBSessionItemOrNull(values, S.COLUMN_HEAD_ARMOR_ID, pieceId)
            ASBSession.BODY -> putASBSessionItemOrNull(values, S.COLUMN_BODY_ARMOR_ID, pieceId)
            ASBSession.ARMS -> putASBSessionItemOrNull(values, S.COLUMN_ARMS_ARMOR_ID, pieceId)
            ASBSession.WAIST -> putASBSessionItemOrNull(values, S.COLUMN_WAIST_ARMOR_ID, pieceId)
            ASBSession.LEGS -> putASBSessionItemOrNull(values, S.COLUMN_LEGS_ARMOR_ID, pieceId)
        }

        return updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }

    fun queryPutASBSessionDecoration(asbSetId: Long, decorationId: Long, pieceIndex: Int, decorationIndex: Int): Long {
        val filter = S.COLUMN_ASB_SET_ID + " = " + asbSetId

        val values = ContentValues()

        when (pieceIndex) {
            ASBSession.HEAD -> if (decorationIndex == 0) {
                putASBSessionItemOrNull(values, S.COLUMN_HEAD_DECORATION_1_ID, decorationId)
            } else if (decorationIndex == 1) {
                putASBSessionItemOrNull(values, S.COLUMN_HEAD_DECORATION_2_ID, decorationId)
            } else if (decorationIndex == 2) {
                putASBSessionItemOrNull(values, S.COLUMN_HEAD_DECORATION_3_ID, decorationId)
            }
            ASBSession.BODY -> if (decorationIndex == 0) {
                putASBSessionItemOrNull(values, S.COLUMN_BODY_DECORATION_1_ID, decorationId)
            } else if (decorationIndex == 1) {
                putASBSessionItemOrNull(values, S.COLUMN_BODY_DECORATION_2_ID, decorationId)
            } else if (decorationIndex == 2) {
                putASBSessionItemOrNull(values, S.COLUMN_BODY_DECORATION_3_ID, decorationId)
            }
            ASBSession.ARMS -> if (decorationIndex == 0) {
                putASBSessionItemOrNull(values, S.COLUMN_ARMS_DECORATION_1_ID, decorationId)
            } else if (decorationIndex == 1) {
                putASBSessionItemOrNull(values, S.COLUMN_ARMS_DECORATION_2_ID, decorationId)
            } else if (decorationIndex == 2) {
                putASBSessionItemOrNull(values, S.COLUMN_ARMS_DECORATION_3_ID, decorationId)
            }
            ASBSession.WAIST -> if (decorationIndex == 0) {
                putASBSessionItemOrNull(values, S.COLUMN_WAIST_DECORATION_1_ID, decorationId)
            } else if (decorationIndex == 1) {
                putASBSessionItemOrNull(values, S.COLUMN_WAIST_DECORATION_2_ID, decorationId)
            } else if (decorationIndex == 2) {
                putASBSessionItemOrNull(values, S.COLUMN_WAIST_DECORATION_3_ID, decorationId)
            }
            ASBSession.LEGS -> if (decorationIndex == 0) {
                putASBSessionItemOrNull(values, S.COLUMN_LEGS_DECORATION_1_ID, decorationId)
            } else if (decorationIndex == 1) {
                putASBSessionItemOrNull(values, S.COLUMN_LEGS_DECORATION_2_ID, decorationId)
            } else if (decorationIndex == 2) {
                putASBSessionItemOrNull(values, S.COLUMN_LEGS_DECORATION_3_ID, decorationId)
            }
            ASBSession.TALISMAN -> if (decorationIndex == 0) {
                putASBSessionItemOrNull(values, S.COLUMN_TALISMAN_DECORATION_1_ID, decorationId)
            } else if (decorationIndex == 1) {
                putASBSessionItemOrNull(values, S.COLUMN_TALISMAN_DECORATION_2_ID, decorationId)
            } else if (decorationIndex == 2) {
                putASBSessionItemOrNull(values, S.COLUMN_TALISMAN_DECORATION_3_ID, decorationId)
            }
        }

        return updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }

    fun queryCreateASBSessionTalisman(asbSetId: Long, type: Int, slots: Int, skill1Id: Long, skill1Points: Int, skill2Id: Long, skill2Points: Int): Long {
        val filter = S.COLUMN_ASB_SET_ID + " = " + asbSetId

        val values = ContentValues()

        values.put(S.COLUMN_TALISMAN_EXISTS, 1)

        values.put(S.COLUMN_TALISMAN_TYPE, type)
        values.put(S.COLUMN_TALISMAN_SLOTS, slots)
        values.put(S.COLUMN_TALISMAN_SKILL_1_ID, skill1Id)
        values.put(S.COLUMN_TALISMAN_SKILL_1_POINTS, skill1Points)

        if (skill2Id != -1L) {
            values.put(S.COLUMN_TALISMAN_SKILL_2_ID, skill2Id)
            values.put(S.COLUMN_TALISMAN_SKILL_2_POINTS, skill2Points)
        } else {
            values.putNull(S.COLUMN_TALISMAN_SKILL_2_ID)
            values.putNull(S.COLUMN_TALISMAN_SKILL_2_POINTS)
        }

        return updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }

    fun queryRemoveASBSessionTalisman(asbSetId: Long): Long {
        val filter = S.COLUMN_ASB_SET_ID + " = " + asbSetId

        val values = ContentValues()

        values.put(S.COLUMN_TALISMAN_EXISTS, 0)

        return updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }

    /**
     * Builds an SQL query that gives us all information about the `ASBSet` in question.
     */
    private fun builderASBSet(): SQLiteQueryBuilder {
        val projectionMap = HashMap<String, String>()

        val set = "ar"

        projectionMap["_id"] = set + "." + S.COLUMN_ASB_SET_ID + " AS " + "_id"

        projectionMap[S.COLUMN_ASB_SET_NAME] = set + "." + S.COLUMN_ASB_SET_NAME
        projectionMap[S.COLUMN_ASB_SET_RANK] = set + "." + S.COLUMN_ASB_SET_RANK
        projectionMap[S.COLUMN_ASB_SET_HUNTER_TYPE] = set + "." + S.COLUMN_ASB_SET_HUNTER_TYPE

        val qb = SQLiteQueryBuilder()
        qb.tables = S.TABLE_ASB_SETS + " AS " + set
        qb.setProjectionMap(projectionMap)

        return qb
    }

    /**
     * Builds an SQL query that gives us all information about the `ASBSession` in question.
     */
    private fun builderASBSession(): SQLiteQueryBuilder {
        val projectionMap = HashMap<String, String>()

        val set = "ar"

        projectionMap["_id"] = set + "." + S.COLUMN_ASB_SET_ID + " AS " + "_id"

        projectionMap[S.COLUMN_HEAD_ARMOR_ID] = set + "." + S.COLUMN_HEAD_ARMOR_ID
        projectionMap[S.COLUMN_HEAD_DECORATION_1_ID] = set + "." + S.COLUMN_HEAD_DECORATION_1_ID
        projectionMap[S.COLUMN_HEAD_DECORATION_2_ID] = set + "." + S.COLUMN_HEAD_DECORATION_2_ID
        projectionMap[S.COLUMN_HEAD_DECORATION_3_ID] = set + "." + S.COLUMN_HEAD_DECORATION_3_ID

        projectionMap[S.COLUMN_BODY_ARMOR_ID] = set + "." + S.COLUMN_BODY_ARMOR_ID
        projectionMap[S.COLUMN_BODY_DECORATION_1_ID] = set + "." + S.COLUMN_BODY_DECORATION_1_ID
        projectionMap[S.COLUMN_BODY_DECORATION_2_ID] = set + "." + S.COLUMN_BODY_DECORATION_2_ID
        projectionMap[S.COLUMN_BODY_DECORATION_3_ID] = set + "." + S.COLUMN_BODY_DECORATION_3_ID

        projectionMap[S.COLUMN_ARMS_ARMOR_ID] = set + "." + S.COLUMN_ARMS_ARMOR_ID
        projectionMap[S.COLUMN_ARMS_DECORATION_1_ID] = set + "." + S.COLUMN_ARMS_DECORATION_1_ID
        projectionMap[S.COLUMN_ARMS_DECORATION_2_ID] = set + "." + S.COLUMN_ARMS_DECORATION_2_ID
        projectionMap[S.COLUMN_ARMS_DECORATION_3_ID] = set + "." + S.COLUMN_ARMS_DECORATION_3_ID

        projectionMap[S.COLUMN_WAIST_ARMOR_ID] = set + "." + S.COLUMN_WAIST_ARMOR_ID
        projectionMap[S.COLUMN_WAIST_DECORATION_1_ID] = set + "." + S.COLUMN_WAIST_DECORATION_1_ID
        projectionMap[S.COLUMN_WAIST_DECORATION_2_ID] = set + "." + S.COLUMN_WAIST_DECORATION_2_ID
        projectionMap[S.COLUMN_WAIST_DECORATION_3_ID] = set + "." + S.COLUMN_WAIST_DECORATION_3_ID

        projectionMap[S.COLUMN_LEGS_ARMOR_ID] = set + "." + S.COLUMN_LEGS_ARMOR_ID
        projectionMap[S.COLUMN_LEGS_DECORATION_1_ID] = set + "." + S.COLUMN_LEGS_DECORATION_1_ID
        projectionMap[S.COLUMN_LEGS_DECORATION_2_ID] = set + "." + S.COLUMN_LEGS_DECORATION_2_ID
        projectionMap[S.COLUMN_LEGS_DECORATION_3_ID] = set + "." + S.COLUMN_LEGS_DECORATION_3_ID

        projectionMap[S.COLUMN_TALISMAN_EXISTS] = set + "." + S.COLUMN_TALISMAN_EXISTS
        projectionMap[S.COLUMN_TALISMAN_TYPE] = set + "." + S.COLUMN_TALISMAN_TYPE
        projectionMap[S.COLUMN_TALISMAN_SLOTS] = set + "." + S.COLUMN_TALISMAN_SLOTS
        projectionMap[S.COLUMN_TALISMAN_SKILL_1_ID] = set + "." + S.COLUMN_TALISMAN_SKILL_1_ID
        projectionMap[S.COLUMN_TALISMAN_SKILL_1_POINTS] = set + "." + S.COLUMN_TALISMAN_SKILL_1_POINTS
        projectionMap[S.COLUMN_TALISMAN_SKILL_2_ID] = set + "." + S.COLUMN_TALISMAN_SKILL_2_ID
        projectionMap[S.COLUMN_TALISMAN_SKILL_2_POINTS] = set + "." + S.COLUMN_TALISMAN_SKILL_2_POINTS
        projectionMap[S.COLUMN_TALISMAN_DECORATION_1_ID] = set + "." + S.COLUMN_TALISMAN_DECORATION_1_ID
        projectionMap[S.COLUMN_TALISMAN_DECORATION_2_ID] = set + "." + S.COLUMN_TALISMAN_DECORATION_2_ID
        projectionMap[S.COLUMN_TALISMAN_DECORATION_3_ID] = set + "." + S.COLUMN_TALISMAN_DECORATION_3_ID

        val qb = SQLiteQueryBuilder()
        qb.tables = S.TABLE_ASB_SETS + " AS " + set
        qb.setProjectionMap(projectionMap)

        return qb
    }

    /**
     * A helper method that determines whether to put `null` or the actual armor id into the table.
     */
    private fun putASBSessionItemOrNull(cv: ContentValues, column: String, pieceId: Long) {
        if (pieceId != -1L) {
            cv.put(column, pieceId)
        } else {
            cv.putNull(column)
        }
    }
}
