# chatbot-whatsapp-notification-service

** Infra Deployment
set RESOURCE_GROUP_NAME=RG-FedExEurope_DeliveryBot
set APP_SERVICE_PLAN_NAME=asp-chatbot-p4ebackend
set APP_SERVICE_NAME=chatbot-p4ebackend
set LOCATION="West Europe"
set LOG_ANALYTICS_WORKSPACE="chatbot-p4ebackend-workspace"
set APP_INSIGHTS_NAME="chatbot-p4ebackend-app"

az appservice plan create --name %APP_SERVICE_PLAN_NAME% --resource-group %RESOURCE_GROUP_NAME% --location %LOCATION% --sku F1 

az webapp create --name %APP_SERVICE_NAME% --resource-group %RESOURCE_GROUP_NAME% --plan %APP_SERVICE_PLAN_NAME% --runtime "java:1.8:Java SE:8"

az webapp log config --application-logging filesystem --detailed-error-messages true --failed-request-tracing true --resource-group %RESOURCE_GROUP_NAME%  --name %APP_SERVICE_NAME% --level verbose --web-server-logging filesystem

az monitor log-analytics workspace create --resource-group %RESOURCE_GROUP_NAME% -n %LOG_ANALYTICS_WORKSPACE%

az monitor app-insights component create --app %APP_INSIGHTS_NAME% --location %LOCATION% --kind web --resource-group %RESOURCE_GROUP_NAME% --application-type web --workspace %LOG_ANALYTICS_WORKSPACE%

az webapp config appsettings set --name %APP_SERVICE_NAME% --resource-group %RESOURCE_GROUP_NAME% --settings APPINSIGHTS_INSTRUMENTATIONKEY=f0f9b402-5db1-421a-9896-051a9b33514a APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=f0f9b402-5db1-421a-9896-051a9b33514a ApplicationInsightsAgent_EXTENSION_VERSION=~2


az monitor diagnostic-settings create --name chatbot-content-service-diagnostic-setting --resource /subscriptions/8c615542-b27e-450a-aad4-c1ba2e5eaf62/resourceGroups/RG-FedExEurope_DeliveryBot/providers/Microsoft.Web/sites/chatbot-content-service --workspace chatbot-content-service-workspace --logs '[ ^
     {^
       "categoryType": "Logs",^
       "name": "AppServiceHTTPLogs",^
       "type": "microsoft.insights/diagnosticSettingsCategories"^
     }^
   ]'^
   --metrics '[^
     {^
       "categoryType": "Metrics",^
       "name": "AllMetrics",^
       "type": "microsoft.insights/diagnosticSettingsCategories"^
     }^
   ]'

** Code Deployment   
 Deployment Via CICD (GitHub Actions)