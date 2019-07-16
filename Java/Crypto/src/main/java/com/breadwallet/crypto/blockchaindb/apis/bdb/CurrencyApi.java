/*
 * Created by Michael Carrara <michael.carrara@breadwallet.com> on 5/31/18.
 * Copyright (c) 2018 Breadwinner AG.  All right reserved.
 *
 * See the LICENSE file at the project root for license information.
 * See the CONTRIBUTORS file at the project root for a list of contributors.
 */
package com.breadwallet.crypto.blockchaindb.apis.bdb;

import androidx.annotation.Nullable;

import com.breadwallet.crypto.blockchaindb.CompletionHandler;
import com.breadwallet.crypto.blockchaindb.models.bdb.Currency;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

public class CurrencyApi {

    private final BdbApiClient jsonClient;

    public CurrencyApi(BdbApiClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    public void getCurrencies(CompletionHandler<List<Currency>> handler) {
        getCurrencies(null, handler);
    }

    public void getCurrencies(@Nullable String id, CompletionHandler<List<Currency>> handler) {
        Multimap<String, String> params = id == null ? ImmutableMultimap.of() : ImmutableListMultimap.of(
                "blockchain_id", id);
        jsonClient.sendGetForArray("currencies", params, Currency::asCurrencies, handler);
    }

    public void getCurrency(String id, CompletionHandler<Currency> handler) {
        jsonClient.sendGetWithId("currencies", id, ImmutableMultimap.of(), Currency::asCurrency, handler);
    }

}
