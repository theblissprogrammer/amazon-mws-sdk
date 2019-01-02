package com.theblissprogrammer.amazon.sdk.stores.products

import androidx.room.Dao
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.price.models.Price

/**
 * Created by ahmed.saad on 2019-01-01.
 * Copyright © 2019. All rights reserved.
 */
@Dao
interface PriceDAO: CommonDAO<Price> {

}

