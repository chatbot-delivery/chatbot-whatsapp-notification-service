# chatbot-whatsapp-notification-service

** Infra Deployment
set RESOURCE_GROUP_NAME=RG-FedExEurope_DeliveryBot
set STORAGE_ACCOUNT_NAME=functionappbotstorage
set APP_SERVICE_PLAN=asp-chatbot-whatsapp-notification-service
set FUNCTION_NAME=chatbot-whatsapp-notification-service
set LOCATION="westeurope"
set LOG_ANALYTICS_WORKSPACE="chatbot-whatsapp-notification-service-workspace"
set APP_INSIGHTS_NAME="chatbot-whatsapp-notification-service-app"

az storage account create -n %STORAGE_ACCOUNT_NAME% --resource-group %RESOURCE_GROUP_NAME% --location %LOCATION% --sku Standard_LRS

az functionapp plan create --name %APP_SERVICE_PLAN% --resource-group %RESOURCE_GROUP_NAME% --location %LOCATION% --sku B1 --min-instances 1

az functionapp create --resource-group %RESOURCE_GROUP_NAME%  -p %APP_SERVICE_PLAN% -n %FUNCTION_NAME% -s %STORAGE_ACCOUNT_NAME% --os-type Windows --runtime java --functions-version 4

#####az functionapp create --consumption-plan-location %LOCATION% --name %FUNCTION_NAME% --os-type Windows --resource-group %RESOURCE_GROUP_NAME% --runtime java --functions-version 4 --storage-account %STORAGE_ACCOUNT_NAME%

az monitor log-analytics workspace create --resource-group %RESOURCE_GROUP_NAME% -n %LOG_ANALYTICS_WORKSPACE%

az monitor app-insights component create --app %APP_INSIGHTS_NAME% --location %LOCATION% --kind web --resource-group %RESOURCE_GROUP_NAME% --application-type web --workspace %LOG_ANALYTICS_WORKSPACE%

az functionapp config appsettings set --name %FUNCTION_NAME% --resource-group %RESOURCE_GROUP_NAME% --settings APPINSIGHTS_INSTRUMENTATIONKEY=af1e15b6-acf0-4d98-900d-a48c4738fec6 APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=af1e15b6-acf0-4d98-900d-a48c4738fec6  PHONE_ID=100845279456984 WHATSAPP_TOKEN=xxxxxx ApplicationInsightsAgent_EXTENSION_VERSION=~2


** Code Deployment   
 Deployment Via CICD (GitHub Actions)
 
 ** Application Url's
 
 https://chatbot-whatsapp-notification-service.azurewebsites.net/api/sendMessage?whtsappno=31626662987&name=saroj&trackingId=12121212&lang=en