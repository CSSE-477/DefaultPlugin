#!/bin/bash

response=$(curl --write-out %{http_code} --silent localhost:8080)
status_code=$(echo "$response" | sed -n '$p')
case "$status_code" in
	200) echo 'curl success!'
		 ;;
	  *) echo 'curl failed!' \
	  	 && service webserver stop \
	  	 && rm /home/csse/YoloSwagSWS/bin/plugin/DefaultPlugin*.jar \
	  	 && service webserver start \
	  	 && echo 'rolled back to previous version'
	  	 exit 1
	  	 ;;
esac
