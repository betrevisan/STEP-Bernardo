// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

//Referenced to https://developers.google.com/maps/documentation/javascript/examples

 
function initMap() {
    var poa = {lat: -30.035, lng: -51.218};
    var map = new google.maps.Map(document.getElementById("map"), {
        zoom: 4,
        center: poa
    });

    var poaString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Porto Alegre</h1>'+
    '<div id="bodyContent">'+
    '<p><b>Porto Alegre</b> is the city I was born in.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Porto_Alegre">'+
    'https://en.wikipedia.org/wiki/Porto_Alegre</a>.</p>'+
    '</div>'+
    '</div>';
    
    var poaMarker = new google.maps.Marker({
        position: poa,
        map: map,
        title: 'Porto Alegre'
    });

    var poaInfo = new google.maps.InfoWindow({
        content: poaString
    });
    
    poaMarker.addListener('click', function() {
        poaInfo.open(map, poaMarker);
    });
}