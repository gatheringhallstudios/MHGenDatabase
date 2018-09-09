package com.ghstudios.android.data.util;

/**
 * An encapsulated of a query to be performed.
 * It is not recommended to use this for new queries, and is only here for backwards compatibility.
 */
public class QueryHelper {
	public boolean Distinct;
	public String Table; 
	public String[] Columns; 
	public String Selection; 
	public String[] SelectionArgs; 
	public String GroupBy;
	public String Having; 
	public String OrderBy; 
	public String Limit;
	
	public QueryHelper()
	{
		Distinct = false;
		Table = null; 
		Columns = null; 
		Selection = null; 
		SelectionArgs = null; 
		GroupBy = null;
		Having = null; 
		OrderBy = null; 
		Limit = null;
	}
	
	public QueryHelper(boolean distinct, String table, String[] columns, 
			String selection, String[] selectionArgs, String groupBy, 
			String having, String orderBy, String limit) {
		Distinct = distinct;
		Table = table; 
		Columns = columns; 
		Selection = selection; 
		SelectionArgs = selectionArgs; 
		GroupBy = groupBy;
		Having = having; 
		OrderBy = orderBy; 
		Limit = limit;
	}
}
