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
    var nhaven = {lat: 41.308, lng: -72.927};
    var beijing = {lat: 39.904, lng: 116.407};
    var treviso = {lat: 45.666, lng: 12.243};
    var singapore = {lat: 1.352, lng: 103.819};
    var munich = {lat: 48.135, lng: 11.582};
    var vancouver = {lat: 49.282, lng: -123.120};
    var lucknow = {lat: 26.846, lng: 80.946};
    var torres = {lat: -29.356, lng: -49.793};
    var sf = {lat: 37.774, lng: -122.419};
    var sp = {lat: -23.550, lng: -46.633};
    var bedford = {lat: 52.138, lng: -0.466};
    var boston = {lat: 42.360, lng: -71.058};

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

    var nhavenString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">New Haven</h1>'+
    '<div id="bodyContent">'+
    '<p><b>New Haven</b> is where I moved to in 2018 to start my undergraduate education at Yale.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/New_Haven,_Connecticut">'+
    'https://en.wikipedia.org/wiki/New_Haven,_Connecticut</a>.</p>'+
    '</div>'+
    '</div>';

    var beijingString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Beijing</h1>'+
    '<div id="bodyContent">'+
    '<p>I spent my Summer of 2019 in <b>Beijing</b> studying Chinese and the local culture.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Beijing">'+
    'https://en.wikipedia.org/wiki/Beijing</a>.</p>'+
    '</div>'+
    '</div>';

    var trevisoString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Treviso</h1>'+
    '<div id="bodyContent">'+
    '<p><b>Treviso</b> is the city my ancestor emigrated from when they moved to  Brazil.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Treviso">'+
    'https://en.wikipedia.org/wiki/Treviso</a>.</p>'+
    '</div>'+
    '</div>';

    var singaporeString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Singapore</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2015, I spent one month in <b>Singapore</b> studying Chinese.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Singapore">'+
    'https://en.wikipedia.org/wiki/Singapore</a>.</p>'+
    '</div>'+
    '</div>';

    var munichString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Munich</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2016, I spent one month in <b>Munich</b> studying German.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Munich">'+
    'https://en.wikipedia.org/wiki/Munich</a>.</p>'+
    '</div>'+
    '</div>';

    var vancouverString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Vancouver</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2014, I flew <b>Vancouver</b> for my first experience abroad, I studied English for a month. This was a particularly important experience to me because it was when I first started thinking about studying abroad.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Vancouver">'+
    'https://en.wikipedia.org/wiki/Vancouver</a>.</p>'+
    '</div>'+
    '</div>';

    var lucknowString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Lucknow</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2016, I participated of QUANTA, an international competition for science, mathematics, astronomy and computer science, in <b>Lucknow</b>.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Lucknow">'+
    'https://en.wikipedia.org/wiki/Lucknow</a>.</p>'+
    '</div>'+
    '</div>';

    var torresString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Torres</h1>'+
    '<div id="bodyContent">'+
    '<p><b>Torres</b> is where my grandmother lives and where I spent most of my Summers growing up.</p>'+
    '<p>For more, visit <a href="https://pt.wikipedia.org/wiki/Torres_(Rio_Grande_do_Sul)">'+
    'https://pt.wikipedia.org/wiki/Torres_(Rio_Grande_do_Sul)</a>.</p>'+
    '</div>'+
    '</div>';

    var sfString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">San Francisco</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2015, I participated of a robotics program in <b>San Francisco</b>. This was a particularly important experience to me because it was when I first realized the huge potential technology has.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/San_Francisco">'+
    'https://en.wikipedia.org/wiki/San_Francisco</a>.</p>'+
    '</div>'+
    '</div>';

    var spString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Sao Paulo</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2017, I participated of the Brazilian Leadership Bootcamp (part of the Latin American Leadership Academy) in <b>Sao Paulo</b>.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/S%C3%A3o_Paulo">'+
    'https://en.wikipedia.org/wiki/S%C3%A3o_Paulo</a>.</p>'+
    '</div>'+
    '</div>';

    var bedfordString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Bedford</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2014, I spent one month in <b>Bedford</b> studying English.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Bedford">'+
    'https://en.wikipedia.org/wiki/Bedford</a>.</p>'+
    '</div>'+
    '</div>';

    var bostonString = '<div id="content">'+
    '<div id="siteNotice">'+
    '</div>'+
    '<h1 id="firstHeading" class="firstHeading">Boston</h1>'+
    '<div id="bodyContent">'+
    '<p>In 2015, I participated of a leadership program at Harvard in <b>Boston</b>.</p>'+
    '<p>For more, visit <a href="https://en.wikipedia.org/wiki/Boston">'+
    'https://en.wikipedia.org/wiki/Boston</a>.</p>'+
    '</div>'+
    '</div>';
    
    var poaMarker = new google.maps.Marker({
        position: poa,
        map: map,
        title: 'Porto Alegre'
    });

    var nhavenMarker = new google.maps.Marker({
        position: nhaven,
        map: map,
        title: 'New Haven'
    });

    var beijingMarker = new google.maps.Marker({
        position: beijing,
        map: map,
        title: 'Beijing'
    });

    var trevisoMarker = new google.maps.Marker({
        position: treviso,
        map: map,
        title: 'Treviso'
    });

    var singaporeMarker = new google.maps.Marker({
        position: singapore,
        map: map,
        title: 'Singapore'
    });

    var munichMarker = new google.maps.Marker({
        position: munich,
        map: map,
        title: 'Munich'
    });

    var vancouverMarker = new google.maps.Marker({
        position: vancouver,
        map: map,
        title: 'Vancouver'
    });

    var lucknowMarker = new google.maps.Marker({
        position: lucknow,
        map: map,
        title: 'Lucknow'
    });

    var torresMarker = new google.maps.Marker({
        position: torres,
        map: map,
        title: 'Torres'
    });

    var sfMarker = new google.maps.Marker({
        position: sf,
        map: map,
        title: 'San Francisco'
    });

    var spMarker = new google.maps.Marker({
        position: sp,
        map: map,
        title: 'Sao Paulo'
    });

    var bedfordMarker = new google.maps.Marker({
        position: bedford,
        map: map,
        title: 'Bedford'
    });

    var bostonMarker = new google.maps.Marker({
        position: boston,
        map: map,
        title: 'Boston'
    });

    var poaInfo = new google.maps.InfoWindow({
        content: poaString
    });

    var nhavenInfo = new google.maps.InfoWindow({
        content: nhavenString
    });

    var beijingInfo = new google.maps.InfoWindow({
        content: beijingString
    });

    var trevisoInfo = new google.maps.InfoWindow({
        content: trevisoString
    });

    var singaporeInfo = new google.maps.InfoWindow({
        content: singaporeString
    });

    var munichInfo = new google.maps.InfoWindow({
        content: munichString
    });

    var vancouverInfo = new google.maps.InfoWindow({
        content: vancouverString
    });

    var lucknowInfo = new google.maps.InfoWindow({
        content: lucknowString
    });

    var torresInfo = new google.maps.InfoWindow({
        content: torresString
    });

    var sfInfo = new google.maps.InfoWindow({
        content: sfString
    });

    var spInfo = new google.maps.InfoWindow({
        content: spString
    });

    var bedfordInfo = new google.maps.InfoWindow({
        content: bedfordString
    });

    var bostonInfo = new google.maps.InfoWindow({
        content: bostonString
    });
    
    poaMarker.addListener('click', function() {
        poaInfo.open(map, poaMarker);
    });

    nhavenMarker.addListener('click', function() {
        nhavenInfo.open(map, nhavenMarker);
    });

    beijingMarker.addListener('click', function() {
        beijingInfo.open(map, beijingMarker);
    });

    trevisoMarker.addListener('click', function() {
        trevisoInfo.open(map, trevisoMarker);
    });

    singaporeMarker.addListener('click', function() {
        singaporeInfo.open(map, singaporeMarker);
    });

    munichMarker.addListener('click', function() {
        munichInfo.open(map, munichMarker);
    });

    vancouverMarker.addListener('click', function() {
        vancouverInfo.open(map, vancouverMarker);
    });

    lucknowMarker.addListener('click', function() {
        lucknowInfo.open(map, lucknowMarker);
    });

    torresMarker.addListener('click', function() {
        torresInfo.open(map, torresMarker);
    });

    sfMarker.addListener('click', function() {
        sfInfo.open(map, sfMarker);
    });

    spMarker.addListener('click', function() {
        spInfo.open(map, spMarker);
    });

    bedfordMarker.addListener('click', function() {
        bedfordInfo.open(map, bedfordMarker);
    });

    bostonMarker.addListener('click', function() {
        bostonInfo.open(map, bostonMarker);
    });
}