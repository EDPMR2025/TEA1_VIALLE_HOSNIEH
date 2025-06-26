package com.pmr2025.viallehosnieh.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.pmr2025.viallehosnieh.data.api.AddItemResponse
import com.pmr2025.viallehosnieh.data.api.AddListResponse
import com.pmr2025.viallehosnieh.data.api.ItemsResponse
import com.pmr2025.viallehosnieh.data.api.ItemsService
import com.pmr2025.viallehosnieh.data.api.ListsService
import com.pmr2025.viallehosnieh.data.api.ListsResponse
import com.pmr2025.viallehosnieh.data.api.UserService
import com.pmr2025.viallehosnieh.data.database.AppDataBase
import com.pmr2025.viallehosnieh.data.database.entities.ItemEntity
import com.pmr2025.viallehosnieh.data.database.entities.ListEntity
import com.pmr2025.viallehosnieh.data.database.entities.PendingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class DataProvider(
    val context : Context
) {
    val ADD_LIST = "addList"
    val ADD_ITEM = "addItem"
    val DELETE_LIST = "deleteList"
    val UPDATE_ITEM_CHECKED = "updateItemChecked"
    val UPDATE_ITEM_NAME = "updateItemName"


    private val BASE_URL = "http://tomnab.fr/todo-api/"

    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val url = prefs.getString("api_url", BASE_URL) ?: BASE_URL

    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val database: AppDataBase = Room.databaseBuilder(
        context = context,
        klass = AppDataBase::class.java,
        name = "todo"
    ).build()

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
            save(listsResponse, hash)
            ResultList.Success(listsResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error fetching lists", e)

            val listEntities = database.listDao().getLists(hash)
            if (listEntities.isEmpty()) {
                ResultList.Failure
            } else {
                ResultList.Success(listEntities.map{
                    ListeToDo(
                        id = it.id,
                        titre = it.label
                    )
                })
            }
        }
    }

    suspend fun getListInfos(hash : String, listId : Int) : ResultList = withContext(Dispatchers.Default) {
        try {
            val listResponse = listsService.getListInfos(hash, listId)
            ResultList.Success(listResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error fetching list infos from API", e)
            val listEntity = database.listDao().getListInfos(listId)
            if (listEntity == null){
                ResultList.Failure
            } else {
                ResultList.Success(listOf(
                    ListeToDo(
                        id = listEntity.id,
                        titre = listEntity.label
                    )
                ))
            }
        }
    }

    suspend fun addList(hash : String, label : String) : ResultList = withContext(Dispatchers.Default) {
        try {
            val listResponse = listsService.addList(hash, label)
            val listEntity = ListEntity(
                label = label,
                id = listResponse.map()[0].id,
                userHash = hash
            )
            database.listDao().insertList(listEntity)
            ResultList.Success(listResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error adding list", e)

            val listEntity = ListEntity(
                label = label,
                userHash = hash
            )
            val localId = database.listDao().insertList(listEntity)
            if (localId == null){
                ResultList.Failure
            } else {
                val pendingEntity = PendingEntity(
                    type = ADD_LIST,
                    hash = hash,
                    label = label
                )
                database.pendingDao().insertPending(pendingEntity)
                ResultList.Success(listOf(
                    ListeToDo(
                        id = localId.toInt(),
                        titre = label
                    )
                ))
            }
        }
    }

    suspend fun deleteList(hash : String, id : Int) : ResultList = withContext(Dispatchers.Default) {
        try {
            listsService.deleteList(hash, id)
            database.listDao().deleteList(ListEntity(id = id, label = "", userHash = ""))
            ResultList.Success(emptyList())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error deleting list", e)

            if (database.listDao().deleteList(ListEntity(id = id, label = "", userHash = "")) == 0) {
                ResultList.Failure
            } else{
                val pendingEntity = PendingEntity(
                    type = DELETE_LIST,
                    hash = hash,
                    listId = id
                )
                database.pendingDao().insertPending(pendingEntity)
                ResultList.Success(emptyList())
            }
        }
    }

    // ITEMS SERVICE

    suspend fun getItems(hash : String, listId : Int) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemsResponse = itemsService.getItems(hash, listId)
            save(itemsResponse, listId)
            ResultItem.Success(itemsResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error fetching items", e)

            val itemEntities = database.itemDao().getItemsFromList(listId)
            if (itemEntities.isEmpty()) {
                ResultItem.Failure
            } else {
                ResultItem.Success(itemEntities.map{
                    ItemToDo(
                        titre = it.label,
                        id = it.id,
                        url = it.url,
                        checked = it.checked == "1"
                    )
                })
            }
        }
    }

    suspend fun addItem(hash : String, listId : Int, label : String) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemResponse = itemsService.addItem(hash, listId, label)
            val itemEntity = ItemEntity(
                id = itemResponse.map()[0].id,
                listId = listId,
                label = label,
                checked = "0",
                url = "")
            database.itemDao().insertItemInList(itemEntity)
            ResultItem.Success(itemResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error adding item", e)

            val itemEntity = ItemEntity(
                listId = listId,
                label = label,
                checked = "0",
                url = "")
           val localId = database.itemDao().insertItemInList(itemEntity)
            if (localId == null){
                ResultItem.Failure
            } else {
                val pendingEntity = PendingEntity(
                    type = ADD_ITEM,
                    hash = hash,
                    listId = listId,
                    label = label
                )
                database.pendingDao().insertPending(pendingEntity)
                ResultItem.Success(listOf(
                    ItemToDo(
                        titre = label,
                        id = localId.toInt(),
                        url = "",
                        checked = false
                    )
                ))
            }
        }
    }

    suspend fun updateItemChecked(hash : String, listId : Int, itemId : Int, checked : String) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemResponse = itemsService.updateItemChecked(hash, listId, itemId, checked = checked)
            database.itemDao().updateItemChecked(itemId, checked)
            ResultItem.Success(itemResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error updating item", e)

            if (database.itemDao().updateItemChecked(itemId, checked) == 0) {
                ResultItem.Failure
            } else {
                val pendingEntity = PendingEntity(
                    type = UPDATE_ITEM_CHECKED,
                    hash = hash,
                    listId = listId,
                    itemId = itemId,
                    checked = checked
                )
                database.pendingDao().insertPending(pendingEntity)
                ResultItem.Success(emptyList())
            }
        }
    }

    suspend fun updateItemName(hash : String, listId : Int, itemId : Int, label : String) : ResultItem = withContext(Dispatchers.Default) {
        try {
            val itemResponse = itemsService.updateItemName(hash, listId, itemId, label = label)
            database.itemDao().updateItemName(itemId, label)
            ResultItem.Success(itemResponse.map())
        } catch (e: Exception) {
            Log.e("DataProvider", "Error updating item", e)
            if (database.itemDao().updateItemName(itemId, label) == 0) {
                ResultItem.Failure
            } else {
                val pendingEntity = PendingEntity(
                    type = UPDATE_ITEM_NAME,
                    hash = hash,
                    listId = listId,
                    itemId = itemId,
                    label = label
                )
                database.pendingDao().insertPending(pendingEntity)
                ResultItem.Success(emptyList())
            }
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

    // Enregistrement dans la base de donénes

    private fun save(listsResponse: ListsResponse, userHash: String) {
        val listEntities = listsResponse.lists.map {
            ListEntity(
                id = it.id,
                label = it.label,
                userHash = userHash
            )
        }
        database.listDao().insertLists(listEntities)
    }

    private fun save(itemsResponse: ItemsResponse, listId: Int) {
        val itemEntities = itemsResponse.items.map {
            ItemEntity(
                id = it.id,
                listId = listId,
                label = it.label,
                checked = it.checked,
                url = it.url
            )
        }
        database.itemDao().insertItemsInList(itemEntities)
    }

    // Synchronisation des actions offline dans l'API

    suspend fun sync() : Int = withContext(Dispatchers.Default){
        val pendingOperations = database.pendingDao().getAllPending()
        pendingOperations.forEach {
            when (it.type){
                ADD_LIST -> {
                    addList(it.hash, it.label.toString())
                }
                ADD_ITEM -> {
                    addItem(it.hash, it.listId!!, it.label.toString())
                }
                DELETE_LIST -> {
                    deleteList(it.hash, it.listId!!)
                }
                UPDATE_ITEM_CHECKED -> {
                    updateItemChecked(it.hash, it.listId!!, it.itemId!!, it.checked.toString())
                }
                UPDATE_ITEM_NAME -> {
                    updateItemName(it.hash, it.listId!!, it.itemId!!, it.label.toString())
                }
            }
        }
        database.pendingDao().deleteAllPending()
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