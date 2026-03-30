Local secrets management

1) Create a local `.env` file (DO NOT commit):

   - Copy `.env.example` to `.env` and fill real values.
   - Minimum required values for local startup:

   ```properties
   ORS_API_KEY=your_openrouteservice_api_key
   DB_URL=jdbc:postgresql://localhost:5432/optimizador_envios
   DB_USERNAME=postgres
   DB_PASSWORD=postgres
   JPA_DDL_AUTO=update
   JPA_SHOW_SQL=false
   ```

2) Load `.env` into your PowerShell session:

   Open PowerShell in project root and run:

   ```powershell
   .\scripts\load-env.ps1
   ./gradlew bootRun
   ```

3) Alternatively, on CMD set the variable for the session:

   ```cmd
   set ORS_API_KEY=mi_api_key && gradlew.bat bootRun
   ```

4) CI / Production: use your platform's secret manager (GitHub Secrets, Vault, etc.)
