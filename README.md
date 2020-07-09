# GithubTest
17Wave-PreTest
- 主要使用：Kotlin、MVVM、LiveData、Paging實現分頁加載

## third-party library：
- CircleImageView for display（de.hdodenhof:circleimageview）
- Glide for load url images（com.github.bumptech.glide:glide）
- Retrofit for networking（com.squareup.retrofit2:retrofit）

## Project-structure:
- api 網路連線相關
- extensions 管理extension
- paging 分頁加載相關
- ui/main 主頁面Fragment、ViewModel、RecyclerViewAdapter

## App使用情境
- 按下搜尋輸入框 -> 輸入搜尋字(會檢查網路與有無輸入空值) -> callApi(30筆/page) -> 顯示結果(有資料：列表頁面 無資料：空結果頁面) -> 如果api有回傳下一個分頁的資訊即可繼續向下繼續滑動顯示加載的資料
- 分頁加載api例外錯誤處理 -> 下方會顯示RETRY按鈕 按下即可重打api

## Code架構解釋
- 當輸入完搜尋字串後按下鍵盤右下搜尋 透過ViewModel去取回Repository的LiveData觀察數據變動時更新畫面UI 
- LiveData:
1. pagedList:更新PagedListAdapter的資料
2. networkLiveData:callApi時的網路狀態

- Repository:
1. 負責使用DataSource.Factory建立dataSource for paging
2. 回傳LiveData給ViewModel

- UserDataSource: paging的資料來源
1. loadInitial(第一次callApi取得資料)
2. loadAfter(page的值永遠是上個page的值+1 callApi加載下一頁資料 note:如果此值大於api回傳的lastPage就不再callApi )
3. callApi Error例外處理: 更新networkLiveData的值處理

- 網路連線:Retrofit
1. Retrofit + Coroutine(async) + moshi(parse json)

## End
