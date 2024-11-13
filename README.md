# mephi

**Proof of concept** for a server reimplementation for **Limbus Company**, written in **Clojure**.

### Features

- **Encryption**: Very similar implementation of the SimpleCrypto module in the original Server namespace.
- **HTTP Server**: Built using Pedestal and Jetty, with the minimum functionality needed to reach the tutorial story.

### Project Structure

- **`src`**: Contains the core source code for the project, including server routes, encryption functions, and main application logic.
- **`deps.edn`**: The Clojure project dependency configuration.


### Clojure Dependencies

- **Pedestal**: For building the HTTP server.
- **Jetty**: For running the server.
- **Buddy Core**: For cryptographic functions.
- **SLF4J**: For logging.

### Setup

1. **Install Clojure**: Make sure you have Clojure installed on your system. You can follow the installation guide [here](https://clojure.org/guides/getting_started).

2. **Clone the repository**:
   ```bash
   git clone https://github.com/linariaworks/mephi.git
   cd mephi
   ```

3. **Install dependencies**:
   ```bash
   clj -P
   ```

4. **Run the server**:
   ```bash
   clj -M:main
   ```

   The server will start on `http://127.0.0.1:9772`.

5. **Proxy Setup**:
   In order for the game to communicate with our server, you will need to configure a proxy to redirect traffic from the game to `http://127.0.0.1:9772`. This is required since the game expects to interact with a server at a specific endpoint, and we are running a local instance.
   You can use tools like [Fiddler](https://www.telerik.com/fiddler) or [mitmproxy](https://mitmproxy.org/) to set up the redirection, or configure your system's network settings accordingly.


### Handled Endpoints

- **POST /login/CheckClientVersion**

- **POST /login/GetTermsOfUseStateAll**

- **POST /login/SignInAsSteam**

- **POST /api/CheckSeasonLog**

- **POST /api/FetchLatestSynchronousData**

- **POST /api/LoadUserDataAll**

- **POST /iap/UpdateSteamPendingPurchase**

### Encryption Details

The encryption method used is an **XOR** operation, combined with **SHA-512** hashing. The steps for encryption are as follows:

1. **Secret Key**: 
   - The secret key `"FACE THE SIN, SAVE THE E.G.O"` is used as the basis for encryption.

2. **SHA-512 Hashing**: 
   - The secret key is hashed using **SHA-512** to create a 512-bit (64-byte) cryptographic hash.

3. **XOR Encryption**:
   - Each byte of the input data is XORed with the corresponding byte of the hashed secret key. If the data length exceeds the length of the key, the key wraps around to ensure all data is encrypted.
   
4. **Initial Offset**:
   - The `encrypted-time` parameter is used to create an offset for the XOR operation. This shifts the starting point of the XOR operation, allowing for different encrypted results even when encrypting the same data.
   - The `encrypted-time` is derived from the current Unix epoch in seconds.
   
5. **Hex Conversion and Wrapping**:
   - After encryption, the resulting byte array is converted into a hexadecimal string using `bytes-to-hex`.
   - The hexadecimal string is then wrapped in double quotes to conform to JSON formatting.

## License

This project is for educational purposes and is released under the **MIT License**.
