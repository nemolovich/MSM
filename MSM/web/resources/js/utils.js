/**
 * Nom de l'application (notamment pour l'URL de navigation)
 * @type String
 */
var appName='MSM';
/**
 * Définir à 'true' pour afficher les détails
 * @type Boolean
 */
var debug=false;
/**
 * Permet d'afficher l'état des requêtes ajax en modal
 * @type Boolean
 */
var displayAjaxStatus=true;
var loaded=false;
var hideTimer;

/**
 * Redéfinit la taille de l'en-tête
 * @param {Number} size - La hauteur de l'en-tête
 * @returns {void}
 */
function setHeaderSize(size)
{
    displayAjaxStatus=false;
    fullPageLayout.layout.sizePane('north',size);
    displayAjaxStatus=true;
}

/**
 * Cache l'en-tête
 * @returns {void}
 */
function hideHeader()
{
    clearTimeout(hideTimer);
    if(!loaded)
    {
        $('#header').slideUp(490,'linear');
        var hideTimer=setTimeout(function()
        {
            setHeaderSize(8);
        }, 500);
        $('#header').fadeIn(5,'linear');
        loaded=true;
    }
}

/**
 * Affiche l'en-tête
 * @returns {void}
 */
function showHeader()
{
    clearTimeout(hideTimer);
    setHeaderSize(92);
    loaded=false;
}

/**
 * Définit les actions à effectuer lors de l'entrée ou de la sortie
 * de la souris sur l'en-tête
 * @returns {void}
 */
function initHeader()
{
    $('#header').mouseenter(function()
    {
        showHeader();
    });
    $('#header').mouseleave(function()
    {
        hideTimer=setTimeout(hideHeader,3000);
    });
}

/**
 * Affiche une fenêtre modale pour le chargement des requêtes ajax
 * @returns {void}
 */
function startAjax()
{
    if(displayAjaxStatus)
    {
        statusDialog.show();
        $('#closeAjax').css('display','block');
    }
}

/**
 * Ferme la fenêtre modale pour le chargement des requêtes ajax
 * @returns {void}
 */
function stopAjax()
{
    if(typeof(statusDialog) !== "undefined")
    {
        statusDialog.hide();
        $('#ajaxStatus_modal').css('display','none');
        $('#closeAjax').css('display','none');
    }
}

/**
 * Ajoute un titre à un élément HTML
 * @param {String} id - Identifiant de l'élément
 * @param {String} title - Titre à ajouter
 * @returns {void}
 */
function addTitle(id, title)
{
    $(document).ready(function()
    {
        $("#"+id).attr('title',title);
    });
}

/**
 * Inspecte le formulaire contenu dans un <p:dialog> pour
 * vérifier s'il est correct.
 * @param {c} form - La boite de dialogue contenant le formulaire
 * @param {Object} xhr - HttpRequest 
 * @param {Object} status - Etat du formulaire
 * @param {String} args - Retour du formulaire
 * @param {Boolean} hide - Indique si on ferme le <p:dialog>
 * @returns {Boolean} - Vrai si le formulaire est correcte
 */
function createRequestFun(form, xhr, status, args, hide)
{
    if(args.validationFailed)
    {
        form.jq.effect("shake", { times:2 }, 100);
        return false;
    }
    else
    {
        if(hide===true)
        {
            form.hide();
        }
        return true;
    }
}
/**
 * @see @link{createRequestFun}
 * @param {c} form - La boite de dialogue contenant le formulaire
 * @param {Object} xhr - HttpRequest 
 * @param {Object} status - Etat du formulaire
 * @param {String} args - Retour du formulaire
 * @returns {Boolean} - Vrai si le formulaire est correcte
 */
function createRequest(form, xhr, status, args)
{
    return createRequestFun(form, xhr, status, args, true);
}

/**
 * @see @link{createRequestFun}
 * De même que la fonction précédente mais ne ferme pas
 * le <p:dialog> après validation du formulaire.
 * @param {c} form - La boite de dialogue contenant le formulaire
 * @param {Object} xhr - HttpRequest 
 * @param {Object} status - Etat du formulaire
 * @param {String} args - Retour du formulaire
 * @returns {Boolean} - Vrai si le formulaire est correcte
 */
function createRequestVoid(form, xhr, status, args)
{
    return createRequestFun(form, xhr, status, args, false);
}

/**
 * Permet de récupérer les évènements venant du clavier via une
 * balise <p:ajax> sur un attribut 'oncomplete' et d'en modifier
 * un objet donné
 * @param {c} item - L'objet à faire intéragir
 * @param {keyboardEvents} event - L'évènement à récupérer
 * @returns {void}
 */
function handleEvent(item, event)
{
    /**
     * Si la touche est 'ESC'
     */
    if(event.keyCode===27)
    {
        item.hide();
        displayAjaxStatus=true;
    }
}

/**
 * Renvoi vrai si la touche pressée est la touche ENTER
 * @param {keyboardEvents} event - L'évènement à récupérer
 * @returns {Boolean} - Vrai si la touche pressée est ENTER
 */
function getEnterKey(event)
{
    if(event.keyCode===13)
    {
        return true;
    }
    return false;
}

/**
 * Exécute un script JavaScript après un intervale donné
 * @param {String} action - Script JavaScript à exécuter
 * @param {Number} secondes - Intervale en secondes
 * @returns {void}
 */
function doAfterInterval(action,secondes)
{
    setTimeout(action,1000*secondes);
}

/**
 * Recharge la page après le nombre de secondes indiquées
 * @param {Number} secondes - Le temps d'attente avant rafraichissement
 * @returns {void}
 */
function reloadInterval(secondes)
{
    doAfterInterval('this.document.location.href=window.location.href',secondes);
}

/**
 * Permet de donner un nom aux données contenues dans un layout (<p:layoutUnit>)
 * pour les titre des leurs boutons d'action
 * @param {String} id - Identifiant du layout
 * @param {String} tips - Nom à afficher en tooltip
 * @returns {void}
 */
function setLayoutButtonsTooltips(id,tips)
{
    $(document).ready(function()
    {
        setTooltips(id,tips);
    });
    $("#"+id+" .ui-layout-unit-header-icon.ui-state-default.ui-corner-all").click(function()
    {
        setTooltips(id,tips);
    });
    $("#"+id+"-resizer").hover(function()
    {
        setTooltips(id,tips);
    });
}

/**
 * Définie le titre des éléments d'action des layouts
 * @param {String} id - Identifiant du layout
 * @param {String} tips - Nom à afficher en tooltip
 * @returns {void}
 */
function setTooltips(id,tips)
{
    $("#"+id+"-resizer").attr('title',"Resize "+tips);
    $("#"+id+"-resizer.ui-layout-resizer-closed").attr('title',"");
    $("#"+id+"-toggler").attr('title',"Show "+tips);
    $("#"+id+" .ui-layout-unit-header-icon.ui-state-default.ui-corner-all:not(#"+id+"_expand)").attr('title',"Hide "+tips);
    $("#"+id+" .ui-layout-unit-header-icon.ui-state-default.ui-corner-all .ui-icon-close").attr('title',"Close "+tips);
}

/**
 * Remplace la valeur d'un champs dans un formulaire
 * par la valeur donnée
 * @param {String} form - Identifiant du formulaire
 * @param {String} id - Identifiant du champs
 * @param {String} value - Valeur à afficher
 * @returns {void}
 */
function setFormFieldValue(form,id,value)
{
    $("#"+form+"\\:"+id).html(value);
}

/**
 * Variable de Timer
 * @type Number
 */
var counter=5;

/**
 * Remonte le timer pour le temps donné
 * @param {Number} time - Temps en secondes
 * @returns {void}
 */
function setTimer(time)
{
    counter=time;
}

/**
 * Met à jour le timer en le décrémentant de 1 seconde
 * @param {String} form - Identifiant du formulaire
 * @param {String} id - Identifiant du champs
 * @returns {void}
 */
function updateTimer(form,id)
{
    setFormFieldValue(form,id,--counter);
    displayAjaxStatus=false;
    if(counter<=0)
    {
        window.location='/'+appName;
    }
}

/**
 * Sélectionne une ligne dans une <p:DataTable> multiSelectable
 * @param {c} table - La table des données
 * @param {Number} index - Index de sélection sur la vue
 * @param {Number} maxRows - Le nombre de lignes par vue
 * @returns {void}
 */
function selectLine(table, index, maxRows)
{
    table.unselectAllRows();
    table.selectRow((index-table.getPaginator().getCurrentPage()*maxRows));
}

//function setOptionSelected(selectId,value)
//{
//    $('#'+selectId+' option:selected').removeAttr("selected");
//    $('#'+selectId+' option[value='+value+']').attr("selected","selected");
//}

/**
 * Renvoi la largeur du navigateur
 * @returns {Number} - Largeur du navigateur
 */
function getNavigatorWidth()
{
    winW=700;
    if (document.body && document.body.offsetWidth)
    {
        winW = document.body.offsetWidth;
    }
    return winW;
}

/**
 * Renvoi la hauteur du navigateur
 * @returns {Number} - Hauteur du navigateur
 */
function getNavigatorHeight()
{
    winH=500;
    if (document.body && document.body.offsetWidth)
    {
        winH = document.body.offsetHeight;
    }
    return winH;
}

/**
 * Permet de forcer le filtre d'une <p:DataTable> en
 * possédant
 * @param {c} table - Table sur laquelle forcer le filtre
 * @returns {void}
 */
function forceFilter(table)
{
    if(typeof( table ) === "undefined")
    {
        return;
    }
    if(table.filter()===undefined)
    {
        if(debug)
        {
            console.log('Force filter for table with id="'+table.jqId.replace(/\\/,'')+'"');
        }
        table.clearFilters();
        table.filter();
    }
}

/**
 * Utilise la valeur d'un <p:input> externe pour la rentrer dans un <p:input>
 * d'une <p:dataTable> afin d'en effectuer un filter
 * @param {String} fromId - Identifiant du <p:input> externe
 * @param {String} toId - Identifiant du <p:input> de la <p:dataTabla>
 * @returns {void}
 */
function filterOutFor(fromId, toId)
{
    var val=$('#'+fromId).val();
    $('#'+toId).val(val);
    $('#'+toId).keyup();
}

/**
 * Permet d'essayer de forcer la mise à jour d'un composant primefaces
 * si celui-ci existe bien
 * @param {String} source - Identifiant de l'élément source
 * @param {String} formId - Identifiant de l'élément à actualiser
 * @returns {void}
 */
function tryUpdate(source, formId)
{
    var id=formId;
    if(id.length>0&&id[0]===':')
    {
        id=id.substring(1,id.length);
    }
    id=id.replace(/:/,"\\\\:");
    var form=$('#'+id);
    if(form.length!==0)
    {
        forceUpdate(source, formId);
    }
}

/**
 * Permet de forcer la mise à jour d'un composant primefaces
 * @param {String} source - Identifiant de l'élément source
 * @param {String} formId - Identifiant de l'élément à actualiser
 * @returns {Boolean} - Toujours faux (réponse javascript d'un bouton)
 */
function forceUpdate(source,formId)
{
    PrimeFaces.ab({source:source,update:'growl '+formId,oncomplete:function(xhr,status,args){},formId:formId});return false;
}

/**
 * Permet de force le tri d'un <p:dataTable> sur la colonne
 * contenant l'identifiant defaultSortColumn
 * @param {String} tableId - Identifiant de la table
 * @returns {void}
 */
function forceSort(tableId)
{
    $('#'+tableId+'\\:defaultSortColumn').click();
}

/**
 * Ajoute un bouton pour ouvrir une <p:dialog> dans un
 * header de menu
 * @param {String} id - Identifiant du menu
 * @param {String} widgetName - Nom du widget <p:dialog>
 * @returns {void}
 */
function addExpandableButton(id, widgetName)
{
    var panel=$("#"+id);
    var parent=panel.find("div.ui-layout-unit-header.ui-widget-header.ui-corner-all");
    var link=document.createElement("a");
    link.className="ui-layout-unit-header-icon-copy ui-state-default ui-corner-all";
    link.id=id+"_expand";
    link.href="javascript:"+widgetName+".show();";
    link.title="Agrandir";
    var span=document.createElement("span");
    span.className="ui-icon ui-icon-extlink";
    link.appendChild(span);
    parent.append(link);
}

/**
 * Ajoute une petite etoile rouge après le label d'un champs
 * pour indiquer qu'il est requis. S'applique sur tous les
 * éléments de la classe 'required'.
 * @returns {void}
 */
function addRedStarsTorequiredFields()
{
    var span=document.createElement("span");
    span.innerHTML="*";
    span.className="red-star";
    $(".required").append(span);
}

/**
 * Remet à zéro un formulaire
 * @param {String} formId - Identifiant du formaulaire
 * @returns {void}
 */
function resetForm(formId)
{
    $('#'+formId).each(function()
    {
        this.reset();
    });
}

/**
 * Variable globale des bloques concurrents
 * @type Array
 */
var concurrents=[];

/**
 * Affiche un bloque et cache ses éventuels concurrents
 * @param {type} id - Identifiant du bloque à afficher
 * @returns {Boolean} - Vrai si tous ses concurrents on bien été cachés
 */
function displayBlock(id)
{
    var ok=true;
    if(containsElement(concurrents,id))
    {
        $.each(concurrents,function(k,v)
        {
            if(id===v[0])
            {
                if(!hideBlock(v[1]))
                {
                    ok=false;
                }
            }
            else if(id===v[1])
            {
                if(!hideBlock(v[0]))
                {
                    ok=false;
                }
            }
        });
    }
    if(ok)
    {
        $("#"+id).css("display","block");
        return true;
    }
    return false;
}

/**
 * Cache un bloque
 * @param {String} id - Identifiant du bloque à chacher
 * @returns {Boolean} - Toujours vrai
 */
function hideBlock(id)
{
    $("#"+id).css("display","none");
    return true;
}

/**
 * Permet de définir des bloques concurrents à celui donné afin de cacher ceux-ci
 * si on veut l'afficher et réciproquement. La liste est enregistrée dans une
 * variable globale.
 * @param {String} id - Identifiant du bloque
 * @param {String[]} listId - Liste des identifiants des bloques concurrents
 * @returns {void}
 */
function addConcurrentBlock(id,listId)
{
    var temp=[];
    $.each(listId,function(i,v)
    {
        temp[temp.length]=v;
        if(!contains(concurrents,[id,v]))
        {
            concurrents[concurrents.length]=[id,v];
        }
    });
    var index=0;
    $.each(temp,function(i,v)
    {
        for(var i=index;i<=temp.length/2;i++)
        {
            if(!contains(concurrents,[temp[i],v])&&temp[i]!==v)
            {
                concurrents[concurrents.length]=[temp[i],v];
            }
        }
        index++;
    });
}

/**
 * Renvoi l'index où se trouve la paire donnée
 * @param {Array} arr - Le tableau dans lequel chercher
 * @param {String[]} pair - La pair à rechercher
 * @returns {Number} - L'index où se trouve la paire dans le tableau
 */
function indexOf(arr,pair)
{
    var index=-1;
    $.each(arr,function(k,v)
    {
        if((pair[0]===v[0]&&pair[1]===v[1])||
            (pair[1]===v[0]&&pair[0]===v[1]))
        {
            index=k;
            return false;
        }
    });
    return index;
}

/**
 * Indique si un tableau contient l'élément donné
 * @param {type} arr - Le tableau dans lequel chercher
 * @param {type} e - L'élément à rechercher
 * @returns {Boolean} - Vrai si le tableau contient l'élément recherché
 */
function containsElement(arr,e)
{
    var contains=false;
    $.each(arr,function(k,v)
    {
        if(e===v[0]||e===v[1])
        {
            contains=true;
            return false;
        }
    });
    return contains;
}

/**
 * Indique si un tableau contient la paire donnée
 * @param {Array} arr - Le tableau dans lequel chercher
 * @param {String[]} pair - La paire à rechercher
 * @returns {Boolean} - Vrai si le tableau contient la paire recherchée
 */
function contains(arr,pair)
{
    return (indexOf(arr,pair)!==-1);
}

/**
 * Renvoi la valeur d'un tableau à un index précis
 * @param {Array} arr - Le tableau dans lequel chercher
 * @param {Number} index - L'index de recherche
 * @returns {String[]} - La paire à l'index donné dans le tableau
 */
function getAt(arr,index)
{
    var value=null;
    $.each(arr,function(k,v)
    {
        if(k===index)
        {
            value=v;
            return false;
        }
    });
    return value;
}

/**
 * Permet de dérouler les menu par défaut au chargement de la page
 * @returns {void}
 */
function expandPanelMenu()
{
    $(document).ready(function()
    {
        $(".ui-panelmenu-content").css("display","block"); //shows the menuitems
        $(".ui-panelmenu-header").addClass("ui-state-active"); //sets the submenu header to active state
        $(".ui-icon-triangle-1-e").removeClass("ui-icon-triangle-1-e").addClass("ui-icon-triangle-1-s"); //sets the triangle icon to "expaned" version
    });
}

/**
 * Affiche l'un ou l'autre des éléments donné suivant l'état donné, le
 * premier élément sera par défaut activé si l'état est à 'true'
 * @param {String} id1 - Identifiant du premier élément
 * @param {String} id2 - Identifiant du second élément
 * @param {String} stateS - État, sous forme de chaine de caractère ('true' ou 'false')
 * @returns {void}
 */
function switchState(id1,id2,stateS)
{
    var state=(stateS!=='false');
    var elm1=null;
    var elm2=null;
    if(id1!=="")
    {
        elm1=$("#"+id1);
    }
    if(id2!=="")
    {
        elm2=$("#"+id2);
    }
    if(state)
    {
        if(elm1!==null)
        {
            elm1.css("display","block");
        }
        if(elm2!==null)
        {
            elm2.css("display","none");
        }
    }
    else
    {
        if(elm1!==null)
        {
            elm1.css("display","none");
        }
        if(elm2!==null)
        {
            elm2.css("display","block");
        }
    }
}

/**
 * Renvoi l'apperçu en heures et minutes d'un temps exprimé en réels
 * @param {Number} value - Valeur réelle
 * @returns {String} - Apperçu en heures et minutes
 */
function getTimeFormat(value)
{
    var h=Math.floor(value);
    var m=(60*(value-h)).toFixed(0);
    if(m>=60)
    {
        m=59;
    }
    var mins=''+m;
    if(m<10)
    {
        mins='0'+m;
    }
    if(m>=0&&h>=0)
    {
        return h+" h "+mins+" mins";
    }
    return "";
}

/**
 * Modifie le contenu d'un élément (<span> ou autre) depuis son identifiant
 * en prenant le contenu d'un champs input (<([ph]*:)input((Text)*)>)
 * @param {String} spanId - Identifiant de l'élément à changer
 * @param {type} inputId - Identifiant du champs <input>
 * @returns {void}
 */
function setHTMLSpan(spanId, inputId)
{
    $('#'+spanId).html(getTimeFormat($("#"+inputId).val()));
}

/**
 * Permet de charger les préférences de langue pour les calendriers
 * primefaces <p:calendar>
 * @returns {void}
 */
function loadLocales()
{
    PrimeFaces.locales['fr'] =
    {
        closeText: 'Fermer',
        prevText: 'Précédent',
        nextText: 'Suivant',
        monthNames: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre' ],
        monthNamesShort: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc' ],
        dayNames: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'],
        dayNamesShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
        dayNamesMin: ['D', 'L', 'M', 'M', 'J', 'V', 'S'],
        weekHeader: 'Semaine',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix:'',
        timeOnlyTitle: 'Choisir une durée',
        timeText: 'Heure',
        hourText: 'Heures',
        minuteText: 'Minutes',
        secondText: 'Secondes',
        currentText: 'Maintenant',
        ampm: false,
        month: 'Mois',
        week: 'Semaine',
        day: 'Jour',
        allDayText: 'Toute la journée'
    };
}

/**!
 * jQuery insertAtIndex
 * project-site: https://github.com/oberlinkwebdev/jQuery.insertAtIndex
 * @author: Jesse Oberlin
 * @version 1.0
 * @param {JQueryElement} $
 * @returns {Boolean}
 * Copyright 2012, Jesse Oberlin
 * Dual licensed under the MIT or GPL Version 2 licenses.
*/
(function ($) { 
$.fn.insertAtIndex = function(index,selector){
    var opts = $.extend({
        index: 0,
        selector: '<div/>'
    }, {index: index, selector: selector});
    return this.each(function() {
        var p = $(this);  
        var i = ($.isNumeric(opts.index) ? parseInt(opts.index) : 0);
        if(i <= 0)
            p.prepend(opts.selector);
        else if( i > p.children().length-1 )
            p.append(opts.selector);
        else
            p.children().eq(i).before(opts.selector);       
    });
};  
})( jQuery );


