package neobis.alier.parking

import io.realm.RealmList
import neobis.alier.parking.models.Data
import retrofit2.Call
import retrofit2.http.GET

interface ForumService {

    @get:GET("b/5abdd3d417c4606cad5ef510")
    val data: Call<RealmList<Data>>
}