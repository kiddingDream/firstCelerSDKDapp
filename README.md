# Android SDK 
# Java doc
https://celer-network.github.io/AndroidApp/index.html
# Hackathon doc
https://docs.google.com/document/d/17xTCVwyPqIiSNYr5dR7n3k9wNzGhXzvzwmdSDpUVbtk/edit?usp=sharing

# Add Payment funtionality to your app 

<img src="https://j.gifs.com/N9M3JL.gif" width="300" />

## API Overview

### Start the app and connect to Celer
In this step, Celer does the following things for you: 
* Prepare ETH account
* Create Celer Client
* Join Celer with deposit

Implement the following code when you start your app.
* client = Celersdk.createNewCelerClient()
* client.joinCeler()

### Display off-chain balance
Implement the following code when want to display users current balance in UI:
* client.getAvailableBalance()

### Send Payment

Implement the following code on your "send" button click event or UI swipe event.
* client.sendPay(amount, destinationAddress)

## Get started

### Step 1. Create a wallet

We have a helper to quickly generate wallets for you. If you already have an account or a wallet, you don't need to use our helper. Alternatively, you can use whatever wallet generation tools you like. 

KeyStoreHelper will create a new ethereum account (wallet).

```kotlin
KeyStoreHelper.generateAccount(this)
val keyStoreString = KeyStoreHelper().getKeyStoreString(this)
val password = KeyStoreHelper().getPassword()
```
Both the keyStoreString and the password will be used to create Celer client in step 3.

### Step 2. Prepare the wallet with some money

Remember that we have already generated new account in the first step, this account does not have any on-chain balance yet.
You can transfer some balance from your existing account if you already have some tokens. 

If you want to have a quick test using Celer's private testnet, you can use Celer's helper class in the OffChainPaymentSample.
```kotlin  
            FaucetHelper().getTokenFromPrivateNetFaucet(context = this,
                    faucetURL = "http://54.188.217.246:3008/donate/",
                    walletAddress = KeyStoreHelper.getAddress(),
                    faucetCallBack = object : FaucetHelper.FaucetCallBack {
                        override fun onSuccess() {
                            showLog("Step 2: getTokenFromFaucet success, wait for transaction to complete")
                        }

                        override fun onFailure() {
                            showLog("getTokenFromFaucet error")
                        }
                    })
```

If you are using Ropsten testnet and want to get some free testnet tokens on Ropsten, here is a quick tutorial to get free ethers on Ropsten:

* Get some free ETH:
Open this link in your browser https://apitester.com/
Put https://faucet.metamask.io/ in the URL field, and choose http POST method.
![Enter URL](https://s3.us-east-2.amazonaws.com/celer-mobile/Screen+Shot+2018-10-08+at+11.05.22+AM.png)

Put the wallet address in the post data field:
![Enter Address](https://s3.us-east-2.amazonaws.com/celer-mobile/Screen+Shot+2018-10-09+at+5.54.12+PM.png)

Click the "Test" button. Metamask will give you 1 ETH. You can see in the http response the transacton id. 

* Check your balance on Ropsten:
https://ropsten.etherscan.io/address/ Append this URL with your wallet address.

For example:
https://ropsten.etherscan.io/address/0x9f6b03cb6d8ab8239cf1045ab28b9df43dfcc823

It may take a while. Please refresh the above link till you see that the balance is no longer in "pending" status.


### Step 3. Create a Celer client

To connect to an off-chain service provider, you need a server profile which is provided by this off-chain service provider. 
For your quick-test convenience, we have prepared everything you need. You can simply use the hard-coded profile in the sample project. “StoreDir” tells the SDK where to cache the data locally on your device. 

```kotlin
val profile = getString(R.string.cprofile, datadir)
```

From the code ,you can see that the profile is a json String for creating Celer client.

Json for private testnet:

```json
    <string name="cprofile">{\"ETHInstance\": \"ws://osp1-test-priv.celer.app:8546\",
        \"SvrRPC\": \"osp1-test-priv.celer.app:10000\",
        \"StoreDir\": \"%1$s\",
        \"SvrETHAddr\": \"5963e46cf9f9700e70d4d1bc09210711ab4a20b4\",
        \"ChanAddr\": \"189908e83d9245d89f0859e8361342b634785956\",
        \"ResolverAddr\": \"1e92cd8c8af5ab6cf5a93824e7f24532fccab5d8\",
        \"DepositPoolAddr\": \"b9353a8413189e3c6c4fce8c48c9b4bd6e5be814\",
        \"HTLRegistryAddr\": \"869ef00c827f91ca115bfe25427b75d970b8d95d\"}</string>
```

Json for Ropsten testnet

```json
{"ETHInstance": "wss://ropsten.infura.io/ws",
 "SvrRPC": "osp1-hack-ropsten.celer.app:10000",
 "StoreDir": "%1$s",
 "SvrETHAddr":  "f805979adde8d63d08490c7c965ee5c1df0aaae2", 
 "ChanAddr": "011b1fa33797be5fcf7f7e6bf436cf99683c186d", 
"ResolverAddr": "cf8938ae21a21a7ffb2d47a69742ef5ce7a669cc",
 "DepositPoolAddr": "658333a4ea7dd461b56592ed62839afc18d54a42",
 "HTLRegistryAddr": "a41bf533110e0b778f6757e04cf7c6d2a8e294b1"}
```

Then we can create a Celer mobile client like this:

```kotlin
CelerClientAPIHelper.initCelerClient(
                    keyStoreString = KeyStoreHelper.getKeyStoreString(),
                    passwordStr = KeyStoreHelper.getPassword(),
                    profile = CelerClientAPIHelper.getProfile(this))
                    
```

Celer client is your starting point to call almost all the methods you need in Celer SDK. 

### Step 4. Join Celer Network

Joining Celer means entering the off-chain world. To join celer, you need to deposit a certain amount of tokens from your on-chain wallet to Celer's state channel, to make sure that you have some off-chain balance to send to others. Meanwhile, server should also deposit certain amount of tokens to the same channel.  

Once you have enough balance, you are good to go with this API call:

```kotlin
// Join Celer Network
try {
   client?.joinCeler("0x0", clientSideDepositAmount, serverSideDepositAmount)
   addLog("Balance: ${client?.getBalance(1)?.available}")
} catch (e: Exception) {
   addLog("Join Celer Network Error: ${e.localizedMessage}")
}
```

Here, “0x0” represents the Ether token. If you need to use ERC20 tokens, please put the contract address instead.

If this process is successful, you will see the balance in the log. A general failure in this process is “Insufficient fund to join celer”, it means that you need to make sure the wallet has enough on-chain balance before joining Celer.

Joining Celer takes some time because it involves some on-chain transactions. The joinCeler function returns a channel id. If you see this channel id, that means your channel is ready to use.  

Congratulations, you are in the off-chain world.


### Step 5. Check balance

```kotlin
val balance = CelerClientAPIHelper.checkBalance()
```

### Step 6. Send off-chain payment

Now that you have opened the channel, you are able to send some off-chain Ether to someone who has also joined Celer.

How do you know that an address has already joined Celer like youself?

```kotlin
// check if an address has joined Celer Network
try {
   val receiverAddr = "0x2718aaa01fc6fa27dd4d6d06cc569c4a0f34d399"
   val hasJoined = client?.hasJoinedCeler(receiverAddress)
   addLog("hasJoined: $hasJoined")
} catch (e: Exception) {
   addLog("check Error: ${e.localizedMessage}")
}
```

The name of “hasJoinedCeler” has not been refactored. It doesn’t explicitly mean whether this address has joined celer (like a boolean value). It is a String representing how much you can send to that address. If it is zero, it means this address has not joined Celer.

If the receiverAddress has joined, you can send some tokens to it

```kotlin
// send cETH to an address
try {
   client?.sendPay(receiverAddress, transferAmount)
} catch (e: Exception) {
   addLog("send cETH Error: ${e.localizedMessage}")
}
```

# Multiplayer Mobile Game 

## API Overview

### Step 1. Start the app and connect to Celer
In this step, Celer does the following things: 
* Prepare ETH account
* Create Celer Client
* Join Celer with deposit

Implement the following code when you start your app.
* client = Mobile.createNewCelerClient()
* client.joinCeler()

### Step 2. Start game session, conditionally pay stake to the other parties
In this step, Celer does the following things for you: 
* Create cApp session
* Send payment with conditions

Implement the following code when your game session has just started:
* client.newCAppSession
* client.sendPaymentWithConditions()

### Step 3. When playing the game, send and receive game state
In this step, Celer does the following things for you: 
* Send state to other players
* Receive state from others

Implement the following code when the user is playing the game.
* client.sendCAppState()
* onReceiveState(byte[] state) 

