(ns rest-crud-demo.controllers.user
  (:require [schema.core :as s]
            [rest-crud-demo.models.user :refer [User]]
            [rest-crud-demo.controllers.auth :refer :all]
            [rest-crud-demo.services.auth :refer :all]            
            [rest-crud-demo.services.user :refer [get-users-handler
                                                  create-user-handler
                                                  get-user-handler
                                                  update-user-handler
                                                  delete-user-handler
                                                  UserRequestSchema]]            
            [toucan.db :as db]
            [compojure.api.sweet :refer [POST GET PUT DELETE context]]))

(def user-routes
  (context "/api/v1/user" []
    :tags ["user"]

    [(POST "/users" []
       :body [create-user-req UserRequestSchema]
       (create-user-handler create-user-req db/insert! User))
     (GET "/users" []
       :auth-roles #{"any"}
       :current-user user
       (get-users-handler db/select User))
     (GET "/users/:id" []
       :path-params [id :- s/Int]
       :auth-roles #{"any"}
       (get-user-handler id User))
     (PUT "/users/:id" []
       :path-params [id :- s/Int]
       :body [update-user-req UserRequestSchema]
       (update-user-handler id update-user-req db/update! User))
     (DELETE "/users/:id" []
       :path-params [id :- s/Int]
       (delete-user-handler id db/delete! User))]))

