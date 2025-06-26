package com.pmr2025.viallehosnieh.data

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.pmr2025.viallehosnieh.data.api.AddItemResponse
import com.pmr2025.viallehosnieh.data.api.AddListResponse
import com.pmr2025.viallehosnieh.data.api.ItemsResponse
import com.pmr2025.viallehosnieh.data.api.ItemsService
import com.pmr2025.viallehosnieh.data.api.ListsService
import com.pmr2025.viallehosnieh.data.api.ListsResponse
import com.pmr2025.viallehosnieh.data.api.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class DataProvider(
    val context : Context
) {
    private val BASE_URL = "http://tomnab.fr/todo-api/"

    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val url = prefs.getString("api_url", BASE_URL) ?: BASE_URL

    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val listsService: ListsService = retrofit.create(ListsService::class.java)
    private val itemsService: ItemsService = retrofit.create(ItemsService::class.java)
    private val userService : UserService = retrofit.create(UserService::class.java)

    // USER SERVICE

    suspend fun authenticate(user : String, password : String) : ResultUser = withContext(Dispatchers.Default) {
        try {
            val hashResponse = userService.authenticate(user, password)
            ResultUser.Success(hash = hashResponse.hash)
        } catch (e: Exception) {
            Log.e("DataProvider", "Error authenticating", e)
            ResultUser.Failure
        }
    }

    // Non implémenté dans la MainActivity car l'API exige d'être connecté afin d'ajouter un nouvel utilisateur.
    suspend fun createUser(user : String, password : String, hash : String) : ResultUser = withContext(Dispatchers.Default) {
        try {
            userService.createUser(user, password, hash)
            ResultUser.Success(hash)
        } catch (e: Exception) {
            Log.e("DataProvider", "Error creating user", e)
            ResultUser.Failure
        }
    }

    // LIST SERVICE

    suspend fun getLists(hash : String): ResultList = withContext(Dispatchers.Default) {
        try {
            val listsResponse = listsService.getLists(hash)
            ResultList.Success(listsResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error fetching lists", e)
            ResultList.Failure
        }
    }

    suspend fun getListInfos(hash : String, id : Int) : ResultList = withContext(Dispatchers.Default) {
        try {
            val listResponse = listsService.getListInfos(hash, id)
            ResultList.Success(listResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error fetching list infos", e)
            ResultList.Failure
        }
    }

    suspend fun addList(hash : String, label : String) : ResultList = withContext(Dispatchers.Default) {
        try {
            val listResponse = listsService.addList(hash, label)
            ResultList.Success(listResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error adding list", e)
            ResultList.Failure
        }
    }

    suspend fun deleteList(hash : String, id : Int) : ResultList = withContext(Dispatchers.Default) {
        try {
            listsService.deleteList(hash, id)
            ResultList.Success(emptyList())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error deleting list", e)
            ResultList.Failure
        }
    }

    // ITEMS SERVICE

    suspend fun getItems(hash : String, listId : Int) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemsResponse = itemsService.getItems(hash, listId)
            ResultItem.Success(itemsResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error fetching items", e)
            ResultItem.Failure
        }
    }

    suspend fun addItem(hash : String, listId : Int, label : String) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemResponse = itemsService.addItem(hash, listId, label)
            ResultItem.Success(itemResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error adding item", e)
            ResultItem.Failure
        }
    }

    suspend fun updateItemChecked(hash : String, listId : Int, itemId : Int, checked : String) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemResponse = itemsService.updateItemChecked(hash, listId, itemId, checked = checked)
            ResultItem.Success(itemResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error updating item", e)
            ResultItem.Failure
        }
    }

    suspend fun updateItemName(hash : String, listId : Int, itemId : Int, label : String) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemResponse = itemsService.updateItemName(hash, listId, itemId, label = label)
            ResultItem.Success(itemResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error updating item", e)
            ResultItem.Failure
        }
    }

    suspend fun deleteItem(hash : String, listId : Int, itemId : Int) : ResultItem = withContext(Dispatchers.Default) {
        try {
            itemsService.deleteItem(hash, listId, itemId)
            ResultItem.Success(emptyList())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error deleting item", e)
            ResultItem.Failure
        }
    }

    // MAPPING

    private fun ListsResponse?.map(): List<ListeToDo> = this?.lists.orEmpty().map { listResponse->
        ListeToDo(
            id = listResponse.id,
            titre = listResponse.label
        )
    }

    private fun AddListResponse.map(): List<ListeToDo> {
        return listOf(
            ListeToDo(
                id = this.list.id,
                titre = this.list.label
            ))
    }

    private fun ItemsResponse?.map(): List<ItemToDo> = this?.items.orEmpty().map { itemResponse ->
        ItemToDo(
            titre = itemResponse.label,
            id = itemResponse.id,
            url = itemResponse.url,
            checked = itemResponse.checked == "1"
        )
    }

    private fun AddItemResponse.map(): List<ItemToDo> {
        return listOf(
            ItemToDo(
                titre = this.item.label,
                id = this.item.id,
                url = this.item.url,
                checked = this.item.checked == "1"
            )
            )
    }
    // Returns

    sealed class ResultUser{
        object Failure : ResultUser()
        data class Success(val hash: String) : ResultUser()
    }

    sealed class ResultList{
        object Failure : ResultList()
        data class Success(val lists: List<ListeToDo>) : ResultList()
    }

    sealed class ResultItem{
        object Failure : ResultItem()
        data class Success(val items: List<ItemToDo>) : ResultItem()
    }
}